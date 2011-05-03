package de.tum.ma.gagern.pseudolines;

abstract class LinComb {

    public abstract void addToEquation(double coeff, Equation eq);

    public abstract double getValue();

    public LinComb add(LinComb that) {
        return new Sum(this, that);
    }

    public LinComb sub(LinComb that) {
        return add(that.mul(-1));
    }

    public LinComb mul(double f) {
        return new Scale(f, this);
    }

    public LinComb div(double f) {
        return new Scale(1./f, this);
    }

    public static LinComb constant(double c) {
        return new Constant(c);
    }

    private static class Sum extends LinComb {

        private final LinComb a;

        private final LinComb b;

        public Sum(LinComb a, LinComb b) {
            this.a = a;
            this.b = b;
        }

        public void addToEquation(double coeff, Equation eq) {
            a.addToEquation(coeff, eq);
            b.addToEquation(coeff, eq);
        }

        public double getValue() {
            return a.getValue() + b.getValue();
        }

    }

    private static class Scale extends LinComb {

        private final double f;

        private final LinComb lc;

        public Scale(double f, LinComb lc) {
            this.f = f;
            this.lc = lc;
        }

        public void addToEquation(double coeff, Equation eq) {
            lc.addToEquation(coeff*f, eq);
        }

        public double getValue() {
            return f*lc.getValue();
        }

    }

    private static class Constant extends LinComb {

        private final double c;

        public Constant(double c) {
            this.c = c;
        }

        public void addToEquation(double coeff, Equation eq) {
            eq.addToRHS(-coeff*c);
        }

        public double getValue() {
            return c;
        }

        public LinComb add(LinComb that) {
            if (c == 0.)
                return that;
            else
                return super.add(that);
        }

    }

}
