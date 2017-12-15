package com.rex.serial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import  java.io.*;

import com.wu.protobuf.AddressBookProtos.AddressBook;
import com.wu.protobuf.AddressBookProtos.Person;

class Parent implements Serializable {

	private static final long serialVersionUID = -4963266899668807475L;

	public int parentValue = 100;
}

class InnerObject implements Serializable {

	private static final long serialVersionUID = 5704957411985783570L;

	public int innerValue = 200;
}

class TestObject extends Parent implements Serializable {

	private static final long serialVersionUID = -3186721026267206914L;

	public int testValue = 300;

	public InnerObject innerObject = new InnerObject();
}

@SpringBootApplication
public class SerialApplication {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileOutputStream fos = new FileOutputStream("temp.out");
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            TestObject testObject = new TestObject();
            oos.writeObject(testObject);
            oos.flush();
            oos.close();


            FileInputStream fis = new FileInputStream("temp.out");
            ObjectInputStream ois = new ObjectInputStream(fis);


            TestObject deTest = (TestObject) ois.readObject();

            System.out.println(deTest.testValue);
            System.out.println(deTest.parentValue);
            System.out.println(deTest.innerObject.innerValue);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("test protobuf ... ");
        SerialApplication demo=new SerialApplication();
        demo.serialize();
        System.out.println("serialize to disk success!!");
        System.out.println("start deserialize......");
        demo.deserialize();

        SpringApplication.run(SerialApplication.class, args);
    }





    public static void Print(AddressBook addressBook) {
        for (Person person: addressBook.getPersonList()) {
            System.out.println("Person ID: " + person.getId());
            System.out.println("  Name: " + person.getName());
            if (person.hasEmail()) {
                System.out.println("  E-mail address: " + person.getEmail());
            }

            for (Person.PhoneNumber phoneNumber : person.getPhoneList()) {
                switch (phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case HOME:
                        System.out.print("  Home phone #: ");
                        break;
                    case WORK:
                        System.out.print("  Work phone #: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }
        }
    }

    public void serialize() throws IOException{
        //build message
        Person john =
                Person.newBuilder()
                        .setId(1234)
                        .setName("John Doe")
                        .setEmail("jdoe@example.com")
                        .addPhone(
                                Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(Person.PhoneType.HOME))
                        .build();

        AddressBook.Builder addressBook = AddressBook.newBuilder();
        addressBook.addPerson(john);

        //serialize to disk
        FileOutputStream output = new FileOutputStream("addressbook.pbd");
        addressBook.build().writeTo(output);
        output.close();
    }


    public void deserialize() throws FileNotFoundException, IOException{
        AddressBook addressBook =
                AddressBook.parseFrom(new FileInputStream("addressbook.pbd"));

        Print(addressBook);
    }

}

