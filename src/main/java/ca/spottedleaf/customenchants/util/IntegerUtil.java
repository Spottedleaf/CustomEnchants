package ca.spottedleaf.customenchants.util;

public class IntegerUtil {

    public static final int HIGH_BIT_U32 = Integer.MIN_VALUE;
    public static final long HIGH_BIT_U64 = Long.MIN_VALUE;

    public static int ceilLog2(final int value) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(value - 1); // see doc of numberOfLeadingZeros
    }

    public static long ceilLog2(final long value) {
        return Long.SIZE - Long.numberOfLeadingZeros(value - 1); // see doc of numberOfLeadingZeros
    }

    public static int floorLog2(final int value) {
        // xor is optimized subtract for 2^n -1
        // note that (2^n -1) - k = (2^n -1) ^ k for k <= (2^n - 1)
        return (Integer.SIZE - 1) ^ Integer.numberOfLeadingZeros(value); // see doc of numberOfLeadingZeros
    }

    public static int floorLog2(final long value) {
        // xor is optimized subtract for 2^n -1
        // note that (2^n -1) - k = (2^n -1) ^ k for k <= (2^n - 1)
        return (Long.SIZE - 1) ^ Long.numberOfLeadingZeros(value); // see doc of numberOfLeadingZeros
    }

    public static int roundCeilLog2(final int value) {
        // optimized variant of 1 << (32 - leading(val - 1))
        // given
        // 1 << n = HIGH_BIT_32 >>> (31 - n) for n [0, 32)
        // 1 << (32 - leading(val - 1)) = HIGH_BIT_32 >>> (31 - (32 - leading(val - 1)))
        // HIGH_BIT_32 >>> (31 - (32 - leading(val - 1)))
        // HIGH_BIT_32 >>> (31 - 32 + leading(val - 1))
        // HIGH_BIT_32 >>> (-1 + leading(val - 1))
        return HIGH_BIT_U32 >>> (Integer.numberOfLeadingZeros(value - 1) - 1);
    }

    public static long roundCeilLog2(final long value) {
        // see logic documented above
        return HIGH_BIT_U64 >>> (Long.numberOfLeadingZeros(value - 1) - 1);
    }

    public static int roundFloorLog2(final int value) {
        // optimized variant of 1 << (31 - leading(val))
        // given
        // 1 << n = HIGH_BIT_32 >>> (31 - n) for n [0, 32)
        // 1 << (31 - leading(val)) = HIGH_BIT_32 >> (31 - (31 - leading(val)))
        // HIGH_BIT_32 >> (31 - (31 - leading(val)))
        // HIGH_BIT_32 >> (31 - 31 + leading(val))
        return HIGH_BIT_U32 >>> Integer.numberOfLeadingZeros(value);
    }

    public static long roundFloorLog2(final long value) {
        // see logic documented above
        return HIGH_BIT_U64 >>> Long.numberOfLeadingZeros(value);
    }

    public static boolean isPowerOfTwo(final int n) {
        // 2^n has one bit
        // note: this rets true for 0 still
        return IntegerUtil.getTrailingBit(n) == n;
    }

    public static boolean isPowerOfTwo(final long n) {
        // 2^n has one bit
        // note: this rets true for 0 still
        return IntegerUtil.getTrailingBit(n) == n;
    }


    public static int getTrailingBit(final int n) {
        return -n & n;
    }

    public static long getTrailingBit(final long n) {
        return -n & n;
    }

    public static int trailingZeros(final int n) {
        return Integer.numberOfTrailingZeros(n);
    }

    public static long trailingZeros(final long n) {
        return Long.numberOfTrailingZeros(n);
    }

    public static int branchlessAbs(final int val) {
        // -n = -1 ^ n + 1
        final int mask = val >> (Integer.SIZE - 1); // -1 if < 0, 0 if >= 0
        return (mask ^ val) - mask; // if val < 0, then (0 ^ val) - 0 else (-1 ^ val) + 1
    }
}