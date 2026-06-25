/**
 * emotions.js
 * Конфигурация эмоций Маруси
 */

const MARUSYA_EMOTIONS = {
    greeting: { video: "greeting.mp4" },
    happy: { video: "happy.mp4" },
    talk: { video: "talk.mp4" },
    angry: { video: "angry.mp4" },
    surprised: { video: "surprised.mp4" },
    thinking: { video: "thinking.mp4" },
    dap: { video: "dap.mp4" },
    loading: { video: "loading.mp4" },
    surprised2: { video: "surprised2.mp4" }
};

const EMOTION_KEYS = Object.keys(MARUSYA_EMOTIONS);

function getRandomEmotion() {
    return EMOTION_KEYS[Math.floor(Math.random() * EMOTION_KEYS.length)];
}

function isValidEmotion(emotion) {
    return EMOTION_KEYS.includes(emotion);
}
