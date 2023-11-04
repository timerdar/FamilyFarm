package dto;

import lombok.Getter;
import lombok.Setter;

public class Consumer {
    @Getter @Setter
    private final String name;
    @Getter @Setter
    private final String street;
    @Getter @Setter
    private final String room;
    @Getter @Setter
    private final String district;
    @Setter @Getter
    private final String phone;

    public Consumer(String name, String street, String room, String district, String phone) {
        this.name = name;
        this.street = street;
        this.room = room;
        this.district = district;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " " + street + " " + room + " " + district + " " + phone;
    }
}
