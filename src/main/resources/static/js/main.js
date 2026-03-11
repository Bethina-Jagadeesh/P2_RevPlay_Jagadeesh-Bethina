/**
 * Global JavaScript for RevPlay
 */

document.addEventListener('DOMContentLoaded', () => {
    console.log('RevPlay Premium UI Initialized');

    // Make the navbar stay correctly (active state & scroll position)
    const currentPath = window.location.pathname;
    const navItems = document.querySelectorAll('.sidebar .nav-item');
    let foundActive = false;

    // Highlight the active link
    const sortedItems = Array.from(navItems).sort((a, b) => {
        const hA = a.getAttribute('href') || "";
        const hB = b.getAttribute('href') || "";
        return hB.length - hA.length;
    });

    sortedItems.forEach(item => {
        const href = item.getAttribute('href');
        if (!foundActive && href && href !== '/' && currentPath.includes(href)) {
            item.classList.add('active');
            foundActive = true;
        } else if (!foundActive && href === '/' && currentPath === '/') {
            item.classList.add('active');
            foundActive = true;
        }
    });

    // Sidebar scroll state
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        const scrollPos = sessionStorage.getItem('sidebarScrollPos');
        if (scrollPos) sidebar.scrollTop = parseInt(scrollPos, 10);
        sidebar.addEventListener('scroll', () => sessionStorage.setItem('sidebarScrollPos', sidebar.scrollTop));
    }
});

function updateActiveNav(urlStr) {
    try {
        const url = new URL(urlStr, window.location.origin);
        const navItems = document.querySelectorAll('.sidebar .nav-item');
        navItems.forEach(item => item.classList.remove('active'));

        const sortedItems = Array.from(navItems).sort((a, b) => {
            return (b.getAttribute('href') || "").length - (a.getAttribute('href') || "").length;
        });

        let foundActive = false;
        sortedItems.forEach(item => {
            const href = item.getAttribute('href');
            if (!foundActive && href && href !== '/' && url.pathname.includes(href)) {
                item.classList.add('active');
                foundActive = true;
            } else if (!foundActive && href === '/' && url.pathname === '/') {
                item.classList.add('active');
                foundActive = true;
            }
        });
    } catch (e) { }
}

function navigateTo(url, push = true) {
    const mainContent = document.querySelector('.main-content');
    if (mainContent) mainContent.style.opacity = '0.5';

    // ── CRITICAL: save the player bar BEFORE any DOM changes ──
    // The innerHTML swap can accidentally pull .player-bar inside .main-content
    // if the browser's DOMParser nests it there. We move it back afterwards.
    const playerBar = document.querySelector('.player-bar');

    fetch(url)
        .then(res => {
            if (!res.ok) {
                if (res.url.includes('/login') || res.status === 401 || res.status === 403) {
                    window.location.href = res.url || url;
                    throw new Error("Redirecting to login");
                }
            }
            return res.text();
        })
        .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');

            document.title = doc.title;
            const newContent = doc.querySelector('.main-content');
            const oldContent = document.querySelector('.main-content');

            if (newContent && oldContent) {
                oldContent.innerHTML = newContent.innerHTML;
                oldContent.style.opacity = '1';
                updateActiveNav(url);
                if (push) {
                    window.history.pushState({}, '', url);
                }

                // ── Ensure the player-bar and playlist-modal are still direct children of <body> ──
                // After innerHTML swap they may have been moved or threatened.
                // Re-anchor them to body so visibility and fixed positioning work correctly.
                if (playerBar) {
                    if (!document.body.contains(playerBar) || playerBar.parentElement !== document.body) {
                        document.body.appendChild(playerBar);
                    }
                }
                const pModal = document.getElementById('playlistModal');
                if (pModal) {
                    if (!document.body.contains(pModal) || pModal.parentElement !== document.body) {
                        document.body.appendChild(pModal);
                    }
                }

                // Execute any new inline scripts from the fetched page
                const scripts = doc.querySelectorAll('script');
                scripts.forEach(s => {
                    if (!s.src) {
                        try {
                            const newScript = document.createElement('script');
                            newScript.textContent = s.textContent;
                            document.body.appendChild(newScript);
                            setTimeout(() => newScript.remove(), 100);
                        } catch (e) {
                            console.error("SPA inline script error:", e);
                        }
                    }
                });

                // ── Restore player footer state ──
                // Re-bind button controls and re-paint current song info.
                setTimeout(() => {
                    if (window.RevPlayer) {
                        window.RevPlayer.bindGlobalControls();
                        if (window.RevPlayer.currentSong) {
                            window.RevPlayer.updateSongInfo(window.RevPlayer.currentSong);
                            window.RevPlayer.updateUI();
                            window.RevPlayer.updateProgressBar();
                        }
                        console.log("RevPlay: SPA nav done, player restored ->", url);
                    }
                }, 50);

            } else {
                console.error("SPA Blocked: .main-content not found for URL:", url);
                if (oldContent) oldContent.style.opacity = '1';
            }
        })
        .catch(err => {
            console.error("SPA Fetch Exception:", err);
            const mainContent = document.querySelector('.main-content');
            if (mainContent) mainContent.style.opacity = '1';
        });
}


// Global click interceptor for SPA navigation
document.addEventListener('click', function (e) {
    if (e.button !== 0 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
    const a = e.target.closest('a');
    if (!a) return;

    const href = a.getAttribute('href');
    if (!href || href.startsWith('#') || href.startsWith('javascript:') || href === '/logout' || a.hasAttribute('download') || a.target === '_blank') return;

    try {
        const targetUrl = new URL(a.href, window.location.origin);
        if (targetUrl.origin !== window.location.origin) return;
        if (targetUrl.pathname.startsWith(window.contextPath + 'uploads')) return;
    } catch(err) {
        return;
    }

    e.preventDefault();
    navigateTo(a.href);
});

window.addEventListener('popstate', function (e) {
    navigateTo(window.location.href, false);
});

/**
 * Handle song like/unlike functionality.
 */
function toggleLike(songId, button) {
    if (!button) return;
    const heart = button.querySelector('.heart-icon');

    console.log("Favorite toggle requested for song ID:", songId);

    fetch(window.contextPath + `user/favorites/toggle/${songId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(res => {
            if (!res.ok) throw new Error("HTTP error " + res.status);
            return res.json();
        })
        .then(data => {
            const isLiked = data.liked;
            console.log("Favorite status for", songId, "is now:", isLiked);

            // Toggle all buttons on screen with this specific data-song-id 
            document.querySelectorAll(`.like-btn[data-song-id="${songId}"]`).forEach(btn => {
                const hIcon = btn.querySelector('.heart-icon');
                if (isLiked) {
                    btn.classList.add('liked');
                    if (hIcon) hIcon.innerText = '❤️';
                } else {
                    btn.classList.remove('liked');
                    if (hIcon) hIcon.innerText = '🤍';

                    if (window.location.pathname.includes('/favorites')) {
                        const row = btn.closest('tr');
                        if (row) {
                            row.style.transition = 'opacity 0.3s ease';
                            row.style.opacity = '0';
                            setTimeout(() => row.style.display = 'none', 300);
                        }
                    }
                }
            });

            // Update footer player if same song is currently loaded
            if (window.RevPlayer && window.RevPlayer.currentSong && window.RevPlayer.currentSong.songId == songId) {
                window.RevPlayer.currentSong.favorite = isLiked;
                window.RevPlayer.currentSong.isFavorite = isLiked;

                const pBtn = document.querySelector('.player-like-btn');
                if (pBtn) {
                    const pIcon = pBtn.querySelector('.heart-icon');
                    if (isLiked) {
                        pBtn.classList.add('liked');
                        if (pIcon) pIcon.innerText = '❤️';
                    } else {
                        pBtn.classList.remove('liked');
                        if (pIcon) pIcon.innerText = '🤍';
                    }
                }
            }
        })
        .catch(err => {
            console.error("Favorite toggle failed on server:", err);
        });
}

function handleSearch(input) {
    const query = input.value.toLowerCase();
    const items = document.querySelectorAll('.music-card, tbody tr');

    items.forEach(item => {
        const text = item.innerText.toLowerCase();
        if (text.includes(query) || query === '') {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

function playPodcast(podcastId) {
    if (window.RevPlayer && typeof window.RevPlayer.playPodcast === 'function') {
        window.RevPlayer.playPodcast(podcastId);
    } else {
        console.error("playPodcast: RevPlayer.playPodcast core missing!");
    }
}
