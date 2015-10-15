package com.bookmate.libs.epub;

import java.util.ArrayList;


/**
 * Created by defuera on 02/07/14.
 */
public class History {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = History.class.getCanonicalName();

    private int index = -1;
    private final ArrayList<ReadState> history = new ArrayList<>();
    private boolean shouldUpdateHistory = true; // RDR document or remove this flag and refactor
    private final ReadingSystem readingSystem;
    private Listener listener;

    public History(ReadingSystem readingSystem) {
        this.readingSystem = readingSystem;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ReadState getCurrentState() {
        return index >= 0 ? history.get(index) : null; // index < 0 can happen when book is just opened and paywall is shown
    }

    public boolean canGoBack() {
        return index > 0;
    }

    public boolean canGoForward() {
        return index < history.size() - 1;
    }

    public void back() {
        if (canGoBack())
            move(false);
    }

    public void forward() {
        if (canGoForward())
            move(true);
    }

    private void move(boolean forward) {
        index += forward ? 1 : -1;
        notifyCurrentState();
        shouldUpdateHistory = false;
    }

    public void notifyCurrentState() {
        listener.onHistoryMove(getCurrentState());
    }


    public void updateCurrentState() {
        if (shouldUpdateHistory)
            updateCurrentState(new ReadState(readingSystem));
        else
            shouldUpdateHistory = true;
    }

    private void updateCurrentState(ReadState state) {
        if (index >= 0) { // history is not empty, so we just update current history state, on scroll page for example
            clearForwardHistory();
            history.remove(index);
            history.add(index, state);
        } else { //history is empty (we just opened book), so add first state
            index++;
            history.add(state);
        }
    }

    public void pushState() {
        pushState(new ReadState(readingSystem));
    }

    private void pushState(ReadState state) {
        index++;
        if (index < history.size()) {
            history.remove(index);
            history.add(index, state);
            clearForwardHistory();
        } else
            history.add(state);
        listener.onHistoryPushState();
    }

    public void clearForwardHistory() {
        int maxIndex = history.size() - 1;
        while (history.size() > index + 1) {
            history.remove(maxIndex);
            maxIndex = history.size() - 1;
        }
    }

    public static class ReadState {
        private final int itemIndex;
        private final float progress;

        public ReadState(ReadingSystem readingSystem) {
            this(readingSystem.getCurrentItemIndex(), readingSystem.progressInItem());
        }

        public ReadState(int itemIndex, float progress) {
            this.itemIndex = itemIndex;
            this.progress = progress;
        }

        public int getItemIndex() {
            return itemIndex;
        }

        public float getProgress() {
            return progress;
        }

        @Override
        public String toString() {
            return itemIndex + "_" + progress;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof ReadState))
                return false;
            ReadState rs = (ReadState) o;

            return rs.itemIndex == itemIndex && rs.progress == progress;
        }

        @Override
        public int hashCode() {
            int result = 17; // Effective Java, p48
            result = 31 * result + itemIndex;
            result = 31 * result + (int) (progress * 1000);
            return result;
        }
    }

    public interface Listener {

        void onHistoryMove(ReadState currentState);

        void onHistoryPushState();
    }
}
