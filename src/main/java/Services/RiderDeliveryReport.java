package Services;

import Domain.Rider;

import java.time.Duration;

public record RiderDeliveryReport(Rider rider, int completedDeliveries, Duration avgDeliveryDuration) {
}
