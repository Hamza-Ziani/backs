package com.veviosys.vdigit.configuration;
import org.springframework.context.ApplicationEvent;

public class AskToExpireSessionEvent extends ApplicationEvent {

        private static final long serialVersionUID = -1915691753338712193L;

        public AskToExpireSessionEvent(final Object source) {
                super(source);
        }

        @Override
        public String getSource() {
                return (String)super.getSource();
        }


        public static AskToExpireSessionEvent of(final String sessionId) {
                return new AskToExpireSessionEvent(sessionId);
        }
}