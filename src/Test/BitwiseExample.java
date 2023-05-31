package Test;

public class BitwiseExample {

    public static void main(String[] args) {

        BitwiseExample obj = new BitwiseExample();
        long ipAddressInLong = obj.ipToLong("192.168.1.2");
        System.out.println(ipAddressInLong);

        String binary = Long.toBinaryString(ipAddressInLong);
        printPrettyBinary(binary);

        String ipAddressInString = obj.longToIp(ipAddressInLong);
        System.out.println(ipAddressInString);

    }

    public long ipToLong(String ipAddress) {

        String[] addrArray = ipAddress.split("\\.");

        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {

            int power = 3 - i;

            // 1. (192 % 256) * 256 pow 3
            // 2. (168 % 256) * 256 pow 2
            // 3. (2 % 256) * 256 pow 1
            // 4. (1 % 256) * 256 pow 0
            num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));

        }

        return num;
    }

    public String longToIp(long i) {

        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);

    }

    //print pretty binary code, padding left zero
    private static void printPrettyBinary(String binary) {

        String s1 = String.format("%32s", binary).replace(' ', '0');
        System.out.format("%8s %8s %8s %8s %n",
                s1.substring(0, 8), s1.substring(8, 16),
                s1.substring(16, 24), s1.substring(24, 32));
    }

}