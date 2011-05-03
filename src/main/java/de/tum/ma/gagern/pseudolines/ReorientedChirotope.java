package de.tum.ma.gagern.pseudolines;

class ReorientedChirotope implements Chirotope {

    final Chirotope delegate;

    final int[] relabeling;

    final int[] reorientation;

    public ReorientedChirotope(Chirotope delegate,
                               int[] relabeling, int[] reorientation) {
        this.delegate = delegate;
        this.relabeling = relabeling;
        this.reorientation = reorientation;
    }

    public int numElements() {
        return delegate.numElements();
    }

    public int chi(int a, int b, int c) {
        return delegate.chi(relabeling[a], relabeling[b], relabeling[c])
            * reorientation[a] * reorientation[b] * reorientation[c];
    }

    public boolean isUniform() {
        return delegate.isUniform();
    }

}
