package namesayer.app.shop;

public class LipCoin {

    private static final int lipCoinsFromEasyActivitiesLimit = 100;

    private static int lipCoins;
    private static int lipCoinsGainedFromEasyActivities;

    public static void setLipCoinsGainedFromEasyActivities(int n) {
        lipCoinsGainedFromEasyActivities = n;
    }

    public static int getLipCoins() {
        return lipCoins;
    }

    public static void setLipCoins(int n) {
        lipCoins = n;
    }

    public static void addLipCoins(String type) {
        switch (type) {
            case "thumbs-down":
                lipCoinsGainedFromEasyActivities += 10;
                if (lipCoinsGainedFromEasyActivities < lipCoinsFromEasyActivitiesLimit) {
                    lipCoins += 10;
                }
                break;

            case "open-app":
                lipCoinsGainedFromEasyActivities += 10;
                if (lipCoinsGainedFromEasyActivities < lipCoinsFromEasyActivitiesLimit) {
                    lipCoins += 10;
                }
                break;

            case "recorded-name":
                lipCoins += 10;
                break;
        }
    }

    public static void addLipCoinsThroughPractice(int n) {
        lipCoins += n * 10;
    }

    public static boolean tryPurchase(int cost) {
        if (lipCoins < cost) {
            return false;
        } else {
            lipCoins -= cost;
            return true;
        }
    }
}
