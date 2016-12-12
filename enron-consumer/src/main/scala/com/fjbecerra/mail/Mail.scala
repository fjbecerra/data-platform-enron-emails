package com.fjbecerra.mail

import scala.io.Source
import com.fjbecerra.mail.Common._


object Mail  {

  /**
    * Clean the text up of an email to extract the body
    * @param body
    * @return
    */
  def exctractBodyMessage(body : String ) = {
    val lines = Source.fromString(body).getLines
      lines.map(_.trim.toLowerCase).map(_.trim)
        .filter(x => !(x startsWith Date))
      .filter(x => !(x startsWith To))
      .filter(x => !(x startsWith From))
      .filter(x => !(x startsWith BCc))
      .filter(x => !(x startsWith Cc))
      .filter(x => !(x startsWith Subject))
      .filter(x => !(x startsWith Xsdoc))
      .filter(x => !(x startsWith XzlidLine))
      .filter(x => !(x startsWith Attachment))
      .filter(x => !(x startsWith  Sent))
      .filter(x => !(x matches EmailPattern.toString()))
      .filter(x => !(x matches OriginalMessage))
      .filter(x => !(x matches  AsterikAndDashes))
      .filter(x => !(x contains LegalTerms))
      .filter(x => !(x matches  ForwardedDashes))
      .filter(x => !(x contains FowardedEndAM))
      .filter(x => !(x contains FowardedEndPM))
      .filter(x => !(x contains FowardedSpell))
      .filter(x => !(x contains  LDAP))
   }

  /**
    * Label the email either forward | reply | original
    * @param subject
    * @return
    */
  def emailType(subject: String) = subject match{
      case null => Some("original")
      case u if u.toLowerCase.startsWith("re:") => Some("reply")
      case u if u.toLowerCase.startsWith("fw:") => Some("forward")
      case _ => Some("original")
  }

  /**
    * Counts the number of words of a line
    * @param text
    * @return
    */
  def countWords(text: String) = {
    text.split("[ ,!.]+").length
  }


  /**
    * Concat lines of a iterator
    * @param iterator
    * @return
    */
  def concatLines(iterator: Iterator[String])= {
     iterator mkString ""
  }

  /**
    * Txt are sent as part of an email. ex. email-id = 12, its txt attached comes as email-id=12.1
    * and they don't have subject
    * @param subject
    * @return
    */
  def filterTxtAttachment(subject: String) = {
    Option(subject).exists(_.trim.nonEmpty)
  }





}
