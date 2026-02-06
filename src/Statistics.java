import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {

    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> existingPages = new HashSet<>();
    private HashSet<String> notFoundPages = new HashSet<>();
    private HashMap<String, Integer> osCount = new HashMap<>();
    private HashMap<String, Integer> browserCount = new HashMap<>();

    private int errorRequestsCount = 0;
    private int nonBotVisitsCount = 0;
    private HashSet<String> nonBotUniqueIPs = new HashSet<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();

        LocalDateTime entryTime = entry.getTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }

        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) {
            errorRequestsCount++;
        }

        UserAgent userAgent = entry.getUserAgent();
        boolean isBot = false;

        if (userAgent != null) {
            String userAgentString = userAgent.getBrowser();
            if (userAgentString != null) {
                String lowerAgent = userAgentString.toLowerCase();
                if (lowerAgent.contains("bot")) {
                    isBot = true;
                }
            }
        }

        if (!isBot) {
            nonBotVisitsCount++;
            nonBotUniqueIPs.add(entry.getIpAddr());
        }

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        if (userAgent != null) {
            String osType = userAgent.getOsType();
            if (osType != null && !osType.isEmpty()) {
                osCount.put(osType, osCount.getOrDefault(osType, 0) + 1);
            }

            String browser = userAgent.getBrowser();
            if (browser != null && !browser.isEmpty()) {
                browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1);
            }
        }
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime) || nonBotVisitsCount == 0){
            return 0.0;
        }

        long hours = getHoursBetweenMinMax();
        if (hours == 0) {
            hours = 1;
        }

        return (double) nonBotVisitsCount / hours;
    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime) || errorRequestsCount == 0){
            return 0.0;
        }

        long hours = getHoursBetweenMinMax();
        if (hours == 0) {
            hours = 1;
        }

        return (double) errorRequestsCount / hours;
    }

    public double getAverageVisitsPerUser() {
        if (nonBotUniqueIPs.isEmpty() || nonBotVisitsCount == 0){
            return 0.0;
        }

        return (double) nonBotVisitsCount / nonBotUniqueIPs.size();
    }

    private long getHoursBetweenMinMax() {
        return Duration.between(minTime, maxTime).toHours();
    }

    public int getErrorRequestsCount() {
        return errorRequestsCount;
    }

    public int getNonBotVisitsCount() {
        return nonBotVisitsCount;
    }

    public int getNonBotUniqueIPsCount() {
        return nonBotUniqueIPs.size();
    }

    public Set<String> getNotFoundPages() {
        return new HashSet<>(notFoundPages);
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStatistics = new HashMap<>();

        if (browserCount.isEmpty()) {
            return browserStatistics;
        }

        int total = 0;
        for (int count : browserCount.values()) {
            total += count;
        }

        for (Map.Entry<String, Integer> entry : browserCount.entrySet()) {
            double share = (double) entry.getValue() / total;
            browserStatistics.put(entry.getKey(), share);
        }

        return browserStatistics;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)){
            return 0.0;
        }
        long hours = getHoursBetweenMinMax();
        if (hours == 0) {
            hours = 1;
        }
        return (double) totalTraffic / hours;
    }

    public int getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public Set<String> getAllExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Map<String, Double> getOperatingSystemStatistics() {
        Map<String, Double> osStatistics = new HashMap<>();

        if (osCount.isEmpty()) {
            return osStatistics;
        }

        int total = 0;
        for (int count : osCount.values()) {
            total += count;
        }

        for (Map.Entry<String, Integer> entry : osCount.entrySet()) {
            double share = (double) entry.getValue() / total;
            osStatistics.put(entry.getKey(), share);
        }

        return osStatistics;
    }
}