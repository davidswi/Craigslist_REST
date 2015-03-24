package com.mcconsulting.demo.model;
import com.orm;

public class Contact extends SugarRecord<Contact> {
	String lastName;
	int age;
	PhoneNumber phoneNumber;
	String firstName;
	Address address;
}
