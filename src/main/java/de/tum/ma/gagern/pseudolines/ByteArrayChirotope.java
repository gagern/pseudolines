package de.tum.ma.gagern.pseudolines;

class ByteArrayChirotope implements Chirotope {

    private static int choose3(int n) {
        // n! / (3! * (n-3)!) = (n * (n-1) * (n-2)) / 6
        return n*(n-1)*(n-2)/6;
    }

    private static int index(int a, int b, int c) {
        assert 0 <= a && a < b && b < c;
        return c*(c-1)*(c-2)/6 + b*(b-1)/2 + a;
    }

    private final int n;

    private final byte[] data;

    public ByteArrayChirotope(CharSequence signs) {
        int n;
        for (n = 3; choose3(n) < signs.length(); ++n);
        int len = choose3(n);
        if (len != signs.length())
            throw new IllegalArgumentException
                ("Not a vaild sign string length");
        this.n = n;
        data = new byte[len];
        int i = 0;
        for (int a = 0; a < n; ++a) {
            for (int b = a + 1; b < n; ++b) {
                for (int c = b + 1; c < n; ++c) {
                    byte sgn;
                    switch (signs.charAt(i)) {
                    case '0':
                        sgn = 0;
                        break;
                    case '+':
                        sgn = 1;
                        break;
                    case '-':
                        sgn = -1;
                        break;
                    default:
                        throw new IllegalArgumentException
                            ("Illegal sign character");
                    }
                    data[index(a, b, c)] = sgn;
                    ++i;
                }
            }
        }
        assert i == len;
        if (!isConsistent())
            throw new IllegalArgumentException("Inconsistent chirotope");
    }

    public int numElements() {
        return n;
    }

    public int chi(int a, int b, int c) {
        int t;
        int s = 1;
        if (a > b) { t = a; a = b; b = t; s = -s; }
        assert a <= b;
        if (b > c) { t = b; b = c; c = t; s = -s; }
        assert a <= c && b <= c;
        if (a > b) { t = a; a = b; b = t; s = -s; }
        assert a <= b && b <= c;
        if (a == b || b == c)
            return 0;
        return s*data[index(a, b, c)];
    }

    private boolean isConsistent() {
        for (int a = 0; a < n; ++a) {
            for (int b = 0; b < n; ++b) {
                if (b == a)
                    continue;
                for (int c = b + 1; c < n; ++c) {
                    if (c == a)
                        continue;
                    int abc = chi(a, b ,c);
                    for (int d = c + 1; d < n; ++d) {
                        if (d == a)
                            continue;
                        int abd = chi(a, b, d);
                        int acd = chi(a, c, d);
                        for (int e = d + 1; e < n; ++e) {
                            if (e == a)
                                continue;
                            int abe = chi(a, b, e);
                            int ace = chi(a, c, e);
                            int ade = chi(a, d, e);
                            int bc_de = abc*ade;
                            int bd_ce = -abd*ace;
                            int be_cd = abe*acd;
                            // if there is one of + or - present, then
                            // the other has to be present as well.
                            if ((bc_de > 0 || bd_ce > 0 || be_cd > 0) !=
                                (bc_de < 0 || bd_ce < 0 || be_cd < 0)) {
                                throw new IllegalArgumentException
                                    ("Chirotope inconsistent at a="
                                    + a + ", b=" + b + ", c=" + c + ", d=" + d
                                    + ", e=" + e);
                                //return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isUniform() {
        for (byte b: data)
            if (b == 0)
                return false;
        return true;
    }

}
