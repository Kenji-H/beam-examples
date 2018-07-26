package com.kenjih.beam.examples;

public class User {
  public String name;
  public int age;
  public char sex;

  @Override
  public String toString() {
    return name + "," + age + "," + sex;
  }
}
