package com.fjbecerra.mail
import java.util.Calendar

import com.fjbecerra.mail.Common._


object Recipient {


  /**
    * Transform emails given firstnames, secondnames lastnames and ldap
    * @param item
    * @return
    */
  def composeEmail(item: String) = item.replaceAll("[\t]", "").trim.toLowerCase match {
      case "" => ""
      case EmailPattern(name ,email) => s"$email"
      case FirstNameLastNamePattern(firstname,lastname ) => s"$firstname.$lastname@enron.com"
      case FirstNameLastNamePattern(firstname, lastname) => s"$firstname.$lastname@enron.com"
      case TwoFirstNameLastNamePattern(lastname,firstname, secondname) => s"$firstname.$secondname.$lastname@enron.com"
      case FirstNameLargeAndShortLastNamePattern(firstname, shortfirstname, lastname) => s"$firstname.$shortfirstname.$lastname@enron.com"
      case email if email.contains("</o=") => email.split("(<)").head.trim.replaceAll("[,.]", "") match {
        case FirstNameLastNamePattern(lastname, firstname) => s"$firstname.$lastname@enron.com"
        case TwoFirstNameLastNamePattern(lastname, firstname, secondname) => s"$firstname.$secondname.$lastname@enron.com"
        case mail => mail
      }
      case email => email
  }

  def composeListOfEmails[A](list: Array[A], relevant:Double) = {

    val l = list.map(x => x.toString)
    for{
      i <- l.indices
      mail= (composeEmail(l(i)), relevant)

    }yield mail

  }

  def extractYearGivenMillisecond(milliseconds: Long): Int = {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(milliseconds)
    calendar.get(Calendar.YEAR)
  }

}






