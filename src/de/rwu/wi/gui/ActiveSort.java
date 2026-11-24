package de.rwu.wi.gui;

final class ActiveSort {
    private SortCancellation active;

    synchronized SortCancellation begin() {
        if (active != null) {
            throw new IllegalStateException("A sorter is already running");
        }
        active = new SortCancellation();
        return active;
    }

    synchronized boolean abort() {
        if (active == null || active.isCancelled()) {
            return false;
        }
        active.cancel();
        return true;
    }

    synchronized boolean finish(SortCancellation cancellation) {
        if (active != cancellation) {
            throw new IllegalStateException("Not the active sorter");
        }
        boolean aborted = cancellation.isCancelled();
        active = null;
        return aborted;
    }
}
