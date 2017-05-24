package com.ch.offloadingjartest.utils;

import com.ch.offloadingjartest.interfaces.DropEggInt;

/**
 * Created by CH on 2017/5/23.
 */

public class DropEgg implements DropEggInt{

    public int fun(int n, int m) {

        if (n == 0) return 0;
        if (n <= 1) return 1;
        if (m == 1) return n;
        int min = n, tmp;
        for (int i = 1; i <= n; ++i) {
            tmp = 1 + Math.max(fun(i - 1, m - 1), fun(n - i, m));
            if(tmp < min)
                min = tmp;
        }
        return min;
    }
}
