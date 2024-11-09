package com.sece;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class TransactionServicesTest {
	TransactionServices ts=new TransactionServices();
   @Test
   public void testGetAccountStatement() throws IOException, SQLException{
	   ts.getAccountStatement(1);
   }
	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
