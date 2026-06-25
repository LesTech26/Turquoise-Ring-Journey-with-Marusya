/**
 * speech.js
 * Голосовое сопровождение (Web Speech API)
 */

class MarusyaSpeech {
    constructor() {
        this.isEnabled = true;
        this.voice = null;
        this.rate = 0.9;
        this.pitch = 1.2;
        this.volume = 1;
        this.isSpeaking = false;
        this.synthesis = window.speechSynthesis || null;
        this.voicesLoaded = false;
        
        this.init();
    }
    
    init() {
        if (this.synthesis) {
            if (this.synthesis.getVoices().length > 0) {
                this.setVoice();
            } else {
                this.synthesis.onvoiceschanged = () => {
                    this.setVoice();
                };
            }
        } else {
            console.warn('Web Speech API не поддерживается в этом браузере');
            this.isEnabled = false;
        }
    }
    
    setVoice() {
        const voices = this.synthesis.getVoices();
        this.voice = voices.find(v => 
            v.lang.toLowerCase().includes('ru') && 
            /female|woman|жен/i.test(v.name)
        ) || voices.find(v => v.lang.toLowerCase().includes('ru')) || voices[0];
        
        this.voicesLoaded = true;
    }
    
    speak(text, options = {}) {
        return new Promise((resolve, reject) => {
            if (!this.isEnabled || !this.synthesis) {
                reject(new Error('Speech synthesis not available'));
                return;
            }
            
            this.cancel();
            
            const utterance = new SpeechSynthesisUtterance(this.prepareText(text));
            utterance.lang = options.lang || 'ru-RU';
            utterance.rate = options.rate || this.rate;
            utterance.pitch = options.pitch || this.pitch;
            utterance.volume = options.volume || this.volume;
            
            if (this.voice) {
                utterance.voice = this.voice;
            }
            
            this.isSpeaking = true;
            
            utterance.onend = () => {
                this.isSpeaking = false;
                resolve();
            };
            
            utterance.onerror = (event) => {
                this.isSpeaking = false;
                if (event.error !== 'canceled') {
                    reject(new Error(`Speech error: ${event.error}`));
                } else {
                    resolve();
                }
            };
            
            this.synthesis.speak(utterance);
        });
    }
    
    cancel() {
        if (this.synthesis) {
            this.synthesis.cancel();
        }
        this.isSpeaking = false;
    }
    
    toggle() {
        this.isEnabled = !this.isEnabled;
        if (!this.isEnabled) {
            this.cancel();
        }
        return this.isEnabled;
    }
    
    static isSupported() {
        return 'speechSynthesis' in window;
    }

    prepareText(text) {
        return String(text)
            .replace(/[\u{1F300}-\u{1FAFF}]/gu, '')
            .replace(/\s+/g, ' ')
            .trim();
    }
}

const marusyaSpeech = new MarusyaSpeech();
