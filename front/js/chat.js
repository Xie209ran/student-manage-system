const ChatPage = {
    template: `
        <div class="chat-page">
            <div class="chat-container">
                <div class="chat-messages" ref="messagesRef">
                    <div v-if="messages.length === 0" class="chat-empty">
                        <el-icon :size="48" color="#909399"><ChatLineSquare /></el-icon>
                        <p class="chat-empty-title">AI Teaching Assistant</p>
                        <p class="chat-empty-desc">Ask me anything about teaching, courses, or students</p>
                    </div>
                    <div v-for="(msg, i) in messages" :key="i" :class="['chat-msg', msg.role]">
                        <div class="msg-avatar">
                            <el-icon v-if="msg.role === 'user'" :size="20"><User /></el-icon>
                            <el-icon v-else :size="20" color="#409EFF"><MagicStick /></el-icon>
                        </div>
                        <div class="msg-bubble">
                            <div class="msg-content">{{ msg.content }}</div>
                        </div>
                    </div>
                    <div v-if="loading" class="chat-msg assistant">
                        <div class="msg-avatar">
                            <el-icon :size="20" color="#409EFF"><MagicStick /></el-icon>
                        </div>
                        <div class="msg-bubble">
                            <div class="msg-content typing">
                                <span class="dot">.</span><span class="dot">.</span><span class="dot">.</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="chat-input-area">
                    <el-input
                        v-model="inputText"
                        type="textarea"
                        :rows="2"
                        placeholder="Type your message..."
                        @keydown.enter.prevent="sendMessage"
                        :disabled="loading"
                        resize="none"
                    ></el-input>
                    <el-button
                        type="primary"
                        :loading="loading"
                        :icon="Promotion"
                        @click="sendMessage"
                        class="send-btn"
                    >Send</el-button>
                </div>
            </div>
        </div>
    `,
    setup() {
        const messages = Vue.ref([]);
        const inputText = Vue.ref('');
        const loading = Vue.ref(false);
        const messagesRef = Vue.ref(null);

        const scrollToBottom = () => {
            Vue.nextTick(() => {
                if (messagesRef.value) {
                    const el = messagesRef.value;
                    el.scrollTop = el.scrollHeight;
                }
            });
        };

        const sendMessage = async () => {
            const text = inputText.value.trim();
            if (!text || loading.value) return;

            messages.value.push({ role: 'user', content: text });
            inputText.value = '';
            loading.value = true;
            scrollToBottom();

            try {
                const result = await http.get('/ai/chat', { message: text });
                messages.value.push({ role: 'assistant', content: result.data || result });
            } catch (e) {
                messages.value.push({ role: 'assistant', content: 'Error: ' + (e.message || 'service unavailable') });
            } finally {
                loading.value = false;
                scrollToBottom();
            }
        };

        return { messages, inputText, loading, messagesRef, sendMessage };
    }
};
