package PageReplacement.Components;

public class Frame{
    private Page page;
    private boolean bitReference;

    public Frame(Page page) {
        this.page = page;
        bitReference = false;
    }

    public Frame() {
        bitReference = false;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public boolean getBitReference() {
        return bitReference;
    }

    public void setBitReference(boolean bitReference) {
        this.bitReference = bitReference;
    }
}
