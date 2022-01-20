package io.those.upnext.model;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class UpNextCalendar implements Comparable<UpNextCalendar> {
    private final Long id;
    private final String name;
    private final String accountName;
    private final String displayName;
    private final Integer color;
    private final Boolean visible;

    public static UpNextCalendar of(Long id, String name, String accountName, String displayName, Integer color, Boolean visible) {
        return new UpNextCalendar(id, name, accountName, displayName, color, visible);
    }

    private UpNextCalendar(Long id, String name, String accountName, String displayName, Integer color, Boolean visible) {
        this.id = id;
        this.name = name;
        this.accountName = accountName;
        this.displayName = displayName;
        this.color = color;
        this.visible = visible;
    }

    @Override
    public int compareTo(UpNextCalendar c) {
        return new CompareToBuilder()
                .append(this.getAccountName(), c.getAccountName())
                .append(this.getId(), c.getId())
                .append(this.getName(), c.getName())
                .toComparison();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getColor() {
        return color;
    }

    public Boolean getVisible() {
        return visible;
    }
}
