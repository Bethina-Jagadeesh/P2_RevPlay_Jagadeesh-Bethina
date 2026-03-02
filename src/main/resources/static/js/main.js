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

                // Execute any new inline scripts
                const scripts = doc.querySelectorAll('script');
                scripts.forEach(s => {
                    if (!s.src) {
                        const newScript = document.createElement('script');
                        newScript.textContent = s.textContent;
                        document.body.appendChild(newScript);
                        setTimeout(() => newScript.remove(), 100);
                    }
                });
            } else {
                window.location.href = url;
            }
        })
        .catch(err => {
            if (err.message !== "Redirecting to login") {
                window.location.href = url;
            }
        });
}

// Global click interceptor for SPA navigation
document.addEventListener('click', function (e) {
    if (e.button !== 0 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
    const a = e.target.closest('a');
    if (!a) return;

    const href = a.getAttribute('href');
    if (!href || href.startsWith('#') || href.startsWith('javascript:') || href === '/logout' || a.hasAttribute('download') || a.target === '_blank') return;

    // Check if it's the same origin
    if (a.origin !== window.location.origin) return;
    if (a.pathname.startsWith('/uploads')) return;

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

    fetch(`/user/favorites/toggle/${songId}`, {
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
