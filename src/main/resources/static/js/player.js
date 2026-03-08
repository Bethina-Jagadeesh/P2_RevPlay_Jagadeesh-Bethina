/**
 * RevPlay Music Player - Standardized Logic
 * Handles playback, UI updates, and queue management.
 */

// Initialize global object immediately
window.RevPlayer = {
    audio: new Audio(),
    isPlaying: false,
    currentQueue: [],
    currentIndex: -1,
    currentSong: null,
    isShuffle: false,
    repeatMode: 'off',
    originalQueue: [],

    init() {
        console.log("RevPlayer: Initializing music core...");

        // Configuration
        this.audio.preload = "auto";
        this.audio.volume = 0.8;
        this.audio.style.display = 'none';
        document.body.appendChild(this.audio);

        // HTML5 Audio Event Listeners
        this.audio.addEventListener('play', () => {
            this.isPlaying = true;
            this.updateUI();
            console.log("RevPlayer: Audio started playing.");
        });

        this.audio.addEventListener('pause', () => {
            this.isPlaying = false;
            this.updateUI();
            console.log("RevPlayer: Audio paused.");
        });

        this.audio.addEventListener('waiting', () => {
            console.log("RevPlayer: Audio is buffering/waiting...");
            const titleE = document.querySelector('.player-title');
            if (titleE && this.currentSong) titleE.innerText = "Buffering... " + this.currentSong.title;
        });

        this.audio.addEventListener('playing', () => {
            console.log("RevPlayer: Audio transition to 'playing' state.");
            if (this.currentSong) this.updateSongInfo(this.currentSong);
        });

        this.audio.addEventListener('timeupdate', () => this.updateProgressBar());
        this.audio.addEventListener('ended', () => this.handleSongEnd());

        this.audio.addEventListener('error', (e) => {
            const error = this.audio.error;
            let message = "Playback Failed";
            if (error) {
                if (error.code === 4) message = "404 - File Not Found";
                else if (error.code === 3) message = "Decoding error (Corrupt MP3?)";
                else if (error.code === 2) message = "Network interrupted";
                else if (error.code === 1) message = "Loading aborted";
            }
            console.error("RevPlayer: Browser reported audio error:", message, error);
            const titleE = document.querySelector('.player-title');
            if (titleE) titleE.innerText = "⚠️ " + message;
        });

        this.bindGlobalControls();
        console.log("RevPlayer: Setup complete. Ready for Port 9090.");
    },

    bindGlobalControls() {
        const attach = (selector, fn) => {
            const el = document.querySelector(selector);
            if (el) el.onclick = (e) => {
                e.preventDefault();
                e.stopPropagation();
                fn.call(this);
            };
        };

        attach('.btn-play', this.togglePlay);
        attach('.player-prev', this.prev);
        attach('.player-next', this.next);
        attach('.btn-shuffle', this.toggleShuffle);
        attach('.btn-repeat', this.toggleRepeat);

        const progressContainer = document.querySelector('.progress-bar');
        if (progressContainer) {
            progressContainer.onclick = (e) => {
                const rect = progressContainer.getBoundingClientRect();
                const percent = (e.clientX - rect.left) / rect.width;
                if (!isNaN(this.audio.duration)) this.audio.currentTime = percent * this.audio.duration;
            };
        }

        const volSlider = document.querySelector('.volume-slider');
        if (volSlider) {
            volSlider.oninput = (e) => { this.audio.volume = e.target.value / 100; };
        }
    },

    play(song, queue = null, fromFetch = false) {
        if (!song || !(song.songId || song.podcastId)) {
            console.error("RevPlayer: Cannot play. ID missing.", song);
            return;
        }

        console.log("RevPlayer: Starting playback for ->", song.title);

        // Update queue management
        if (queue && Array.isArray(queue) && queue.length > 0) {
            const isDifferentSet = (this.originalQueue.length !== queue.length) ||
                this.originalQueue.some((s, i) => (s.songId || s.podcastId) !== (queue[i].songId || queue[i].podcastId));

            if (isDifferentSet) {
                console.log("RevPlayer: Loading new queue of", queue.length, "items.");
                this.originalQueue = [...queue];
                this.currentQueue = this.isShuffle ? this.shuffleArray([...queue]) : [...queue];
            }

            const sid = song.songId || song.podcastId;
            this.currentIndex = this.currentQueue.findIndex(s => (s.songId || s.podcastId) == sid);
            console.log("RevPlayer: Active index in queue:", this.currentIndex);
        } else if (!this.currentSong || this.currentQueue.length <= 1) {
            this.currentQueue = [song];
            this.originalQueue = [song];
            this.currentIndex = 0;
        }

        this.currentSong = song;

        // 1. Update UI immediately
        this.updateSongInfo(song);
        this.updateUI();

        // 2. Resolve and Load Audio
        if (song.fileUrl) {
            let finalUrl = song.fileUrl.replace(/\\/g, '/');
            if (!finalUrl.startsWith('http')) {
                const prefix = finalUrl.startsWith('/') ? '' : '/';
                finalUrl = window.location.origin + prefix + finalUrl;
            }

            // Escaping special characters (? # etc) by encoding the filename part
            let urlParts = finalUrl.split('/');
            let filename = urlParts.pop();
            finalUrl = urlParts.join('/') + '/' + encodeURIComponent(filename);

            console.log("RevPlayer: Final Encoded URI ->", finalUrl);

            this.audio.pause();
            this.audio.src = finalUrl;
            this.audio.load();

            const playPromise = this.audio.play();
            if (playPromise !== undefined) {
                playPromise.then(() => {
                    console.log("RevPlayer: Successfully playing.");
                    this.isPlaying = true;
                    this.updateUI();
                }).catch(error => {
                    console.error("RevPlayer: Playback blocked/failed.", error);
                    this.isPlaying = false;
                    this.updateUI();
                    const titleE = document.querySelector('.player-title');
                    if (titleE) titleE.innerText = "Click to Play: " + song.title;
                });
            }
        } else {
            console.log("RevPlayer: URL missing in object, requesting from server.");
            if (song.isPodcast) this.playPodcast(song.songId || song.podcastId);
            else this.fetchAndPlay(song.songId);
        }

        // 3. Increment play count (Background)
        if (!fromFetch && !song.isPodcast) {
            fetch(`/user/play/${song.songId || song.podcastId}`, { method: 'POST' }).catch(() => { });
        }
    },

    playPodcast(podcastId) {
        console.log("RevPlayer: playPodcast action for ID", podcastId);

        // Check page cache
        if (window.pagePodcasts && Array.isArray(window.pagePodcasts)) {
            const p = window.pagePodcasts.find(x => x.podcastId == podcastId);
            if (p && p.fileUrl) {
                const q = window.pagePodcasts;
                this.play({
                    songId: p.podcastId,
                    title: p.title,
                    artistName: p.hostName || ("Host ID: " + p.artistId),
                    fileUrl: p.fileUrl,
                    coverImageUrl: p.coverImageUrl || "",
                    isPodcast: true
                }, q, true);
                return;
            }
        }

        console.log("RevPlayer: Loading podcast details from server...");
        fetch(`/user/play/podcast/${podcastId}`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data && data.fileUrl) {
                    data.isPodcast = true;
                    // Get all podcasts for queue if possible
                    const q = (window.pagePodcasts && Array.isArray(window.pagePodcasts)) ? window.pagePodcasts : [data];
                    this.play(data, q, true);
                } else {
                    alert("Podcast audio file not found on server.");
                }
            })
            .catch(err => console.error("RevPlayer: Podcast fetch failed", err));
    },

    fetchAndPlay(songId) {
        console.log("RevPlayer: Fetching song details for ID:", songId);
        fetch(`/user/play/${songId}`, { method: 'POST' })
            .then(r => {
                if (!r.ok) throw new Error("Server error " + r.status);
                return r.json();
            })
            .then(song => {
                if (song && song.fileUrl) {
                    this.play(song, null, true);
                } else {
                    console.error("RevPlayer: Song object or fileUrl missing", song);
                    const titleE = document.querySelector('.player-title');
                    if (titleE) titleE.innerText = "❌ File location missing";
                }
            })
            .catch(err => {
                console.error("RevPlayer: Fetch failed", err);
                const titleE = document.querySelector('.player-title');
                if (titleE) titleE.innerText = "❌ Connection Failed";
            });
    },

    togglePlay() {
        if (!this.audio.src) return;
        if (this.isPlaying) {
            this.audio.pause();
        } else {
            this.audio.play().then(() => {
                this.isPlaying = true;
                this.updateUI();
            }).catch(e => {
                console.error("RevPlayer: Play failed on toggle", e);
                this.isPlaying = false;
                this.updateUI();
            });
        }
    },

    next() {
        if (this.currentQueue.length === 0) return;
        this.currentIndex = (this.currentIndex + 1) % this.currentQueue.length;
        console.log("RevPlayer: Next Track. Index:", this.currentIndex);
        this.play(this.currentQueue[this.currentIndex], null, true);
    },

    prev() {
        if (this.currentQueue.length === 0) return;
        if (this.audio.currentTime > 5) {
            this.audio.currentTime = 0;
            return;
        }
        this.currentIndex = (this.currentIndex - 1 + this.currentQueue.length) % this.currentQueue.length;
        console.log("RevPlayer: Prev Track. Index:", this.currentIndex);
        this.play(this.currentQueue[this.currentIndex], null, true);
    },

    toggleShuffle() {
        this.isShuffle = !this.isShuffle;
        console.log("RevPlayer: Shuffle turned", this.isShuffle ? "ON" : "OFF");

        if (this.currentSong && this.originalQueue.length > 0) {
            const currentId = this.currentSong.songId || this.currentSong.podcastId;

            if (this.isShuffle) {
                this.currentQueue = this.shuffleArray([...this.originalQueue]);
            } else {
                this.currentQueue = [...this.originalQueue];
            }
            this.currentIndex = this.currentQueue.findIndex(s => (s.songId || s.podcastId) == currentId);
        }
        this.updateUI();
    },

    toggleRepeat() {
        const modes = ['off', 'all', 'one'];
        this.repeatMode = modes[(modes.indexOf(this.repeatMode) + 1) % modes.length];
        this.updateUI();
    },

    handleSongEnd() {
        console.log("RevPlayer: Song ended. Mode:", this.repeatMode);
        if (this.repeatMode === 'one') {
            this.audio.currentTime = 0;
            this.audio.play();
        } else if (this.repeatMode === 'all' || this.currentIndex < this.currentQueue.length - 1) {
            this.next();
        } else {
            this.isPlaying = false;
            this.updateUI();
        }
    },

    toggleCurrentLike() {
        if (this.currentSong && typeof toggleLike === 'function') {
            toggleLike(this.currentSong.songId, document.querySelector('.player-like-btn'));
        } else {
            console.warn("RevPlayer: toggleLike function not found or no song playing.");
        }
    },

    updateSongInfo(song) {
        if (!song) return;
        const titleE = document.querySelector('.player-title');
        const artistE = document.querySelector('.player-artist-name');
        const artE = document.querySelector('.player-art');
        const heartBtn = document.querySelector('.player-like-btn');

        if (titleE) titleE.innerText = song.title;
        if (artistE) artistE.innerText = song.artistName || 'RevPlay';

        if (artE) {
            if (song.coverImageUrl) {
                artE.style.backgroundImage = `url('${song.coverImageUrl}')`;
                artE.style.backgroundSize = 'cover';
                artE.style.backgroundPosition = 'center';
            } else {
                artE.style.backgroundImage = 'linear-gradient(135deg, #1DB954, #121212)';
            }
        }

        if (heartBtn) {
            heartBtn.style.display = 'block';
            const icon = heartBtn.querySelector('.heart-icon');
            const liked = song.favorite || song.isFavorite;
            heartBtn.classList.toggle('liked', liked);
            if (icon) icon.innerText = liked ? '❤️' : '🤍';
        }
    },

    updateUI() {
        const pBtn = document.querySelector('.btn-play');
        if (pBtn) {
            pBtn.innerHTML = this.isPlaying ? '<span>⏸</span>' : '<span>▶</span>';
            pBtn.title = this.isPlaying ? 'Pause' : 'Play';
        }

        const sBtn = document.querySelector('.btn-shuffle');
        if (sBtn) {
            sBtn.style.color = this.isShuffle ? '#1DB954' : '#b3b3b3';
            sBtn.title = this.isShuffle ? 'Shuffle On' : 'Shuffle Off';
        }

        const rBtn = document.querySelector('.btn-repeat');
        if (rBtn) {
            rBtn.style.color = this.repeatMode !== 'off' ? '#1DB954' : '#b3b3b3';
            rBtn.innerText = this.repeatMode === 'one' ? '🔂' : '🔁';
            
            let repeatTitle = 'Repeat Off';
            if (this.repeatMode === 'one') repeatTitle = 'Repeat One';
            else if (this.repeatMode === 'all') repeatTitle = 'Repeat All';
            rBtn.title = repeatTitle;
        }
    },

    updateProgressBar() {
        const fill = document.querySelector('.progress-fill');
        const curr = document.querySelector('.time-current');
        const total = document.querySelector('.time-total');

        if (fill && !isNaN(this.audio.duration) && this.audio.duration > 0) {
            fill.style.width = ((this.audio.currentTime / this.audio.duration) * 100) + '%';
        }
        if (curr) curr.innerText = this.formatTime(this.audio.currentTime);
        if (total && !isNaN(this.audio.duration) && this.audio.duration > 0) {
            total.innerText = this.formatTime(this.audio.duration);
        }
    },

    formatTime(sec) {
        if (isNaN(sec) || sec <= 0) return "0:00";
        const m = Math.floor(sec / 60);
        const s = Math.floor(sec % 60);
        return `${m}:${s < 10 ? '0' : ''}${s}`;
    },

    shuffleArray(arr) {
        for (let i = arr.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [arr[i], arr[j]] = [arr[j], arr[i]];
        }
        return arr;
    }
};

/**
 * Universal click-to-play interface used by all templates
 */
window.playSong = function (songId) {
    if (!songId) return;
    const sId = Number(songId);
    console.log("playSong: Action triggered for ID", sId);

    if (!window.RevPlayer) {
        console.error("RevPlayer core is missing from windows.");
        return;
    }

    let track = null;
    let list = null;

    // Scan page-level data injected by Thymeleaf
    const sources = ['allSongs', 'pageSongs', 'searchSongs', 'favoriteSongs', 'songsInPlaylist'];
    for (const src of sources) {
        if (Array.isArray(window[src])) {
            track = window[src].find(s => s.songId == sId);
            if (track) {
                list = window[src];
                break;
            }
        }
    }

    if (track) {
        console.log("playSong: Found track in " + (list ? "list" : "singleton"));
        window.RevPlayer.play(track, list);
    } else {
        console.log("playSong: Track not in cache, fetching from server...");
        window.RevPlayer.fetchAndPlay(sId);
    }
};

/**
 * Debugging helper for the user
 */
window.debugRevPlayer = function () {
    console.log("--- RevPlayer Debug Report ---");
    console.log("Audio Source:", window.RevPlayer.audio.src);
    console.log("Is Playing:", window.RevPlayer.isPlaying);
    console.log("Current Time:", window.RevPlayer.audio.currentTime);
    console.log("Duration:", window.RevPlayer.audio.duration);
    console.log("readyState:", window.RevPlayer.audio.readyState);
    console.log("networkState:", window.RevPlayer.audio.networkState);
    console.log("Current Song Object:", window.RevPlayer.currentSong);
    console.log("Available data lists:", ['allSongs', 'pageSongs', 'searchSongs', 'favoriteSongs', 'songsInPlaylist'].filter(s => window[s]));
    return "Report complete. Check the values above.";
};

// Start the core engine
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => window.RevPlayer.init());
} else {
    window.RevPlayer.init();
}

