package com.example.sestas_aukstas.ework;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserTests {

    UserClass test = new UserClass("a1b2c3d4e5", "Martynas", "martynas@gmail.com");

    @Test
    public void getUsrName() {
        assertEquals("Martynas", test.getUserName());
    }

    @Test
    public void setUserName(){
        test.setUserName("Tadas");
        assertEquals("Tadas", test.getUserName());
    }

    @Test
    public void setUserNameInvalid(){
        test.setUserName("Tadas");
        assertNotEquals("Martynas", test.getUserName());
    }

    @Test
    public void getUserID() {assertEquals("a1b2c3d4e5", test.getUserID());}

    @Test
    public void getUserMail() {assertEquals("martynas@gmail.com", test.getUserMail());}

    @Test
    public void setUserMail(){
        test.setUserMail("tadas@gmail.com");
        assertEquals("tadas@gmail.com", test.getUserMail());
    }

    @Test
    public void setUserMailInvalid(){
        test.setUserMail("tadas@gmail.com");
        assertNotEquals("martynas@gmail.com", test.getUserMail());
    }

}