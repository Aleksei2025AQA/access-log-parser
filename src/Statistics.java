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
    private HashMap<String, Integer> osCount = new HashMap<>();

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

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }


        UserAgent userAgent = entry.getUserAgent();
        if (userAgent != null) {
            String osType = userAgent.getOsType();
            if (osType != null && !osType.isEmpty()) {
                osCount.put(osType, osCount.getOrDefault(osType, 0) + 1);
            }
        }
    }


    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0.0;
        }
        long hours = Duration.between(minTime, maxTime).toHours();
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