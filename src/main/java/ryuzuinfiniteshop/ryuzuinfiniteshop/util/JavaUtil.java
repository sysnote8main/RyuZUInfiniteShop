package ryuzuinfiniteshop.ryuzuinfiniteshop.util;

import java.util.ArrayList;
import java.util.List;

public class JavaUtil {
    public static <T> List<T> getSubList(List<T> list, int from, int to) {
        int toIndex = Math.min(to, list.size());
        return new ArrayList<>(list.subList(from, toIndex));
    }

    public static <T> List<T>[] splitList(List<T> list, int n) {
        int size = list.size();
        int m = size / n;
        if (size % n != 0) m++;

        List<T>[] splitted = new ArrayList[m];
        for (int i = 0; i < m; i++) {
            int fromIndex = i * n;
            int toIndex = Math.min(i * n + n, size);

            splitted[i] = new ArrayList<>(list.subList(fromIndex, toIndex));
        }

        return splitted;
    }
}
