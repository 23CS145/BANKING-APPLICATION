package com.sece;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BankingServicesTest {
    BankingServices banking=new BankingServices();
	@Test
	public void loginWithValidCredentials() {
        boolean result = banking.adminLogin("Sara", "Sara@123");
        assertEquals(result, true);
    }
	
	
	@Test
	public void loginWithInValidCredentials() {
        boolean result = banking.adminLogin("Sara", "Sara");
        assertEquals(result, false);
        
    }
	



}
