public class UserAgent {
    private final String osType;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.osType = extractOsType(userAgentString);
        this.browser = extractBrowser(userAgentString);
    }

    public String getOsType() {
        return osType;
    }

    public String getBrowser() {
        return browser;
    }

    private String extractOsType(String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("windows")) {
            return "Windows";
        } else if (ua.contains("mac os") || ua.contains("macos")) {
            return "macOS";
        } else if (ua.contains("linux")) {
            return "Linux";
        } else if (ua.contains("android")) {
            return "Android";
        } else if (ua.contains("ios")) {
            return "iOS";
        } else {
            return "Other";
        }
    }

    private String extractBrowser(String userAgent) {
        String ua = userAgent.toLowerCase();

        if (ua.contains("googlebot")) {
            return "Googlebot";
        } else if (ua.contains("yandexbot")) {
            return "YandexBot";
        }

        if (ua.contains("edge") || ua.contains("edg/")) {
            return "Edge";
        } else if (ua.contains("firefox") || ua.contains("fxios")) {
            return "Firefox";
        } else if (ua.contains("chrome") && !ua.contains("chromium")) {
            return "Chrome";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "Safari";
        } else if (ua.contains("opera") || ua.contains("opr/")) {
            return "Opera";
        } else if (ua.contains("yandex")) {
            return "Yandex";
        } else {
            return "Other";
        }
    }

    @Override
    public String toString() {
        return "UserAgent{osType='" + osType + "', browser='" + browser + "'}";
    }
}