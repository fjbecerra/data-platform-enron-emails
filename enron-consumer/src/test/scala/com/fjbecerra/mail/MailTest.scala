package com.fjbecerra.mail

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.scalatest.mock.MockitoSugar
import Mail._



class MailTest extends JUnitSuite with MockitoSugar {

  @Test def givenALineStartingWithDateColumnWhenFilteredThenItIsGone(){
    val text = "Date: 20-06-2016"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithSentColumnWhenFilteredThenItIsGone(){
    val text = "Sent: 20-06-2016"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithToColumnWhenFilteredTheItISGone(){
    val text = "To: fran.bcra@gmail.com"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithFromColumnWhenFilteredTheItISGone(){
    val text = "From: fran.bcra@gmail.com"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithBccColumnWhenFilteredThenLineIsGone(){
    val text = "BCC: fran.bcra@gmail.com"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithCCclumnWhenFilteredThenLineIsGone(){
    val text = "CC: fran.bcra@gmail.com, Jhon@gmail.com"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithAttachmentlumnWhenFilteredThenLineIsGone(){
    val text = "Attachment: tutorial.pdf"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingWithXdocColumnWhenFilteredThenLineIsGone(){
    val text = "x-sdoc: tutorial.doc"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenALineStartingXzlidColumnWhenFilteredThenLineIsGone(){
    val text = "x-zlid: anything"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingLdapWhenFilteredThenLineIsGone(){
    val text = "nm </o= anything"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineMatchingWrapEmailsWhenFilteredThenLineIsGone(){
    val text = "<fran.bcra@gmaail.com> fran, <Julian@gmail.com> julian"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineMatchingBareEmailsWhenFilteredThenLineIsGone(){
    val text = "fran.bcra@gmaail.com, Julian@gmail.com"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineMatchingDashesWhenFilteredThenLineIsGone(){
    val text = "-------"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineMatchingAsteriksWhenFilteredThenLineIsGone(){
    val text = "***************************"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingOriginalMessageWhenFilteredThenLineIsGone(){
    val text = "-----Original Message-----"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingDashesForwardWhenFilteredThenLineIsGone(){
    val text = "---------------------- Forwarded"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingForwardAMWhenFilteredThenLineIsGone(){
    val text = "AM ---------------------------"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingForwardPMWhenFilteredThenLineIsGone(){
    val text = "PM ---------------------------"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingForwardSpellWhenFilteredThenLineIsGone(){
    val text = "FYI"
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenLineContainingLegalTermsWhenFilteredThenLineIsGone(){
    val text = "\"EDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to \"ZL Technologies, Inc. (http://www.zlti.com).\""
    assert(!exctractBodyMessage(text).contains(text))
  }

  @Test def givenTextMessageWhenCleanUpReturn33Lines {
    val listCleaned = exctractBodyMessage("Date: Thu, 18 Oct 2001 07:55:58 -0700 (PDT)\nFrom: Lamadrid, Victor </O=ENRON/OU=NA/CN=RECIPIENTS/CN=VLAMADR>\nTo: Ames, Chuck </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Cames>, Brawner, Sandra F. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Sbrawne>, Germany, Chris </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Cgerman>, Goodell, Scott </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Sgoodel>, Hodge, John </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jhodge2>, Keavey, Peter\n\t F. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Pkeavey>, Mckay, Brad </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Bmckay>, Mckay, Jonathan </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jmckay1>, Neal, Scott </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Sneal>, Pereira, Susan\n\t W. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Spereir>, Pimenov, Vladi </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Vpimenov>, Ring, Andrea </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Aring>, Savvas, Leonidas </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Lsavvas>, Smith, Maureen </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Mgreena>, Taylor, Craig </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jtaylo2>, Townsend, Judy </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jtownse>, Versen, Victoria </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Vversen>, Vickers, Frank </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Fvicker>\nCc: Garcia, Clarissa </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Cgarcia>, Smith, George\n\t F. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Gsmith>, Villarreal, Jesse </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Jvillar>, Kelly, Katherine\n\t L. </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Kkelly>, Khandker, Dayem </O=ENRON/OU=NA/CN=RECIPIENTS/CN=Dkhandke>\nSubject: FW: TE Restriction for Oct 18-19\nX-SDOC: 788131\nX-ZLID: zl-edrm-enron-v2-townsend-j-213.eml\n\nfyi\n\n-----Original Message-----\nFrom: Boudreaux, Shanna \nSent: Thursday, October 18, 2001 9:31 AM\nTo: Townsend, Judy; Germany, Chris; Lamadrid, Victor; Neal, Scott;\nVersen, Victoria; Ordway, Chris\nSubject: FW: TE Restriction for Oct 18-19\n\n\nFYI...\n\n\nOctober 18, 2001\n\nAn unplanned outage at the ATHENS (ATH) Ohio Compressor station has\noccurred.  Due to high throughput levels already in the system and the\nrestriction at ATH, TEXAS EASTERN TRANSMISSION, LP (TE) will not be\naccepting physical increases for Gas Day October 18, 2001 upstream of\nATH.  This would include the entire Access Area, Market Zone 1 30 Inch &\n24 Inch, Market Zone 2 upstream of the ATH compressor station.  Due Pipe\nmakeup cannot be accepted for Gas Day October 19,2001.  These\nrestrictions may be extended through the weekend.   Meter operators are\nreminded to flow at scheduled volumes.\n\nShould you have any questions, please contact your Operations Account\nManager.\n\n\n\n*******************************************************************\n\nNOTE:\n\nDuke Energy Gas Transmission respects your online time and privacy.\nYou have received this email because you elected to subscribe. To\nunsubscribe, login to the E-mail Notification Subscription page at\nhttp://www.link.duke-energy.com/script2/Notification.asp\nuncheck the appropriate checkbox, and click the Submit button.\n\n***********\nEDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to \"ZL Technologies, Inc. (http://www.zlti.com).\"\n***********")
    assert(listCleaned.size == 33)
  }

  @Test def givenEmailReColumnWithInTheSubjectThenLabTheMailReplyType(){
    val text = "re: enron emails"
    assert(emailType(text) === Some("reply"))
  }

  @Test def givenEmailFwColumnWithInTheSubjectThenLabTheMailForwardType(){
    val text = "fw: enron emails"
    assert(emailType(text) === Some("forward"))
  }

  @Test def givenEmailFwColumnWithInTheSubjectThenLabTheMailOriginalType(){
    val text = "enron emails"
    assert(emailType(text) === Some("original"))
  }

  @Test def givenNullInTheSubjectThenLabTheMailOriginalType(){
    val text = null
    assert(emailType(text) === Some("original"))
  }

  @Test def givenALineWhenCountThenReturnNumberWordsWithinTheLine(){
    val text = "hello, this is a test!"
    assert(countWords(text) === 5)
  }

  @Test def givenListOfLineWhenConcatThenMatchWhaExpected(){
    val lines = Iterator("hello", ", world" )
    assert(concatLines(lines) == "hello, world")
  }

  @Test def givenAEmptySubjectWhenFilterThenIsAnTxtAttached(){
    val subject = null
    assert(!filterTxtAttachment(subject))

  }

  @Test def givenASubjectWhenFilterThenIsNotTxtAttached(){
    val subject = "this is a test"
    assert(filterTxtAttachment(subject))

  }

}