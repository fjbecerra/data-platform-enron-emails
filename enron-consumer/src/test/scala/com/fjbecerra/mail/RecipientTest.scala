package com.fjbecerra.mail

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.MockitoSugar

class RecipientTest extends JUnitSuite with MockitoSugar{

  @Test def givenFirstAndLastNameWhenComposeEmailThenMatchesExpected(){
     val email = "Judy Townsend"
     assert("judy.townsend@enron.com" === Recipient.composeEmail(email))
  }

  @Test def givenWrappedEmailWhenComposeEmailThenMatchesExpected(){
    val email = "Townsend  Judy <Judy.Townsend@ENRON.com>"
    assert("judy.townsend@enron.com" === Recipient.composeEmail(email))
  }

  @Test def givenTwoFirstNameWrappedEmailWhenComposeEmailThenMatchesExpected(){
    val email = "Townsend  Judy J. <Judy.j.Townsend@ENRON.com>"
    assert("judy.j.townsend@enron.com" === Recipient.composeEmail(email))
  }

  @Test def giveBareEmailWhenComposeEmailThenMatchesExpected(){
    val email = "jtownse@yahoo.com"
    assert("jtownse@yahoo.com" === Recipient.composeEmail(email))
  }

  @Test def given2FirstAnd1LastNameWhenComposeEmailThenMatchesExpected(){
    val email = "Pereira Susan W"
    assert("susan.w.pereira@enron.com" === Recipient.composeEmail(email))
  }

  @Test def given1CompleteAndShortFirstNameAnd1LastNameWhenComposedThenMatchedExpected(): Unit ={
    val email = "Rebecca W Cantrell"
    assert("rebecca.w.cantrell@enron.com" === Recipient.composeEmail(email))

  }

  @Test def givenLastNameCommaWhenComposeEmailThenMatchExpected(){
   val email ="Marshall, Howard  </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Hmarshal>"

    assert("howard.marshall@enron.com" === Recipient.composeEmail(email))
  }

  @Test def givenLastNameCommaTwoFirstNameWhenComposeEmailThenMatchExpected(){
    val email ="Driscoll, Michael\t M. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Mdrisc3>"

    assert("michael.m.driscoll@enron.com" === Recipient.composeEmail(email))
  }

  @Test def givenIMCEANOTESEmailWhenComposeThenReturnItSelf(){
    val email ="sap_security@enron.com@ENRON <IMCEANOTES-sap+5Fsecurity+40enron+2Ecom+40ENRON@ENRON.com>"
    assert("imceanotes-sap+5fsecurity+40enron+2ecom+40enron@enron.com" === Recipient.composeEmail(email))
  }

  @Test def givenListOfEmailsWhenComposeThenReturnThoseEmailsHaveBeenComposed(): Unit ={
     val list = Array("JhonBrown@enron.com", "Judy Townsend")
    assert(Array(("jhonbrown@enron.com",1), ("judy.townsend@enron.com",1)) === Recipient.composeListOfEmails(list,1))
  }

  @Test def givenDateInMillisecWhenExtractYearThenReturnTheCorrectYear(): Unit ={
    assertResult(2016)(Recipient.extractYearGivenMillisecond(1465081200000l))
  }


}
