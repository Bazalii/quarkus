package io.quarkus.it.kafka.fruit;

public class FruitDto {
    String name;

    public FruitDto(Fruit fruit) {
        this.name = fruit.name;
    }

    public FruitDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
