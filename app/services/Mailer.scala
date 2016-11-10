package services

import java.net.URL
import javax.mail.internet.InternetAddress

import models.BookingEnquiryForm
import org.apache.commons.mail.resolver.DataSourceUrlResolver
import org.apache.commons.mail.{DefaultAuthenticator, ImageHtmlEmail}
import org.joda.time.format.DateTimeFormat
import play.api.{Logger, Play}

object Mailer {
  
  lazy val conf = Play.current.configuration
  lazy val host = conf.getString("smtp.host").getOrElse("127.0.0.1")
  lazy val port = conf.getInt("smtp.port").getOrElse(25)
  lazy val ssl  = conf.getBoolean("smtp.ssl").getOrElse(false)
  lazy val tls  = conf.getBoolean("smtp.tls").getOrElse(false)
  lazy val user = conf.getString("smtp.user")
  lazy val pass = conf.getString("smtp.password")
  lazy val bookingEnquiryDest = conf.getString("mail.bookingEnquiry.dest").getOrElse("nic@nicshouse.co.uk")
  lazy val mailer = SmtpMailer(host, port, ssl, tls, user, pass)

    
  def mailContactForm(bookingEnquiryForm: BookingEnquiryForm) = {

    val to = List( bookingEnquiryDest )
    val from = s"Website <noreply@funkylimos.com>"
    val subject = s"Booking enquiry from ${bookingEnquiryForm.fullName}"
    val text = List(
      "Name" -> bookingEnquiryForm.fullName,
      "Email address" -> bookingEnquiryForm.email,
      "Telephone" -> bookingEnquiryForm.telephone,
      "Event date" -> DateTimeFormat.forPattern("dd/MM/YYYY").print(bookingEnquiryForm.getDate),
      "Vehicle requested" -> bookingEnquiryForm.vehicle,
      "Address" -> bookingEnquiryForm.address.replace("\n", ", "),
      "Comments" -> bookingEnquiryForm.comments.getOrElse("")
    ).map( x => x._1 + ": " + x._2 ).mkString("\n")

    val html = views.html.emails.bookingEnquiry.render(subject, bookingEnquiryForm).body
    //sends both text and html versions of email
    Some(mailer.send(text, html, subject, from, to))
  }
}

case class SmtpMailer(smtpHost: String, smtpPort: Int, smtpSsl: Boolean = false, smtpTls: Boolean = false,
                      smtpUser: Option[String] = None, smtpPass: Option[String] = None, charSet: String = "utf-8") {

  /**
   * Sends an email based on the provided data.
   *
   * @param bodyText : pass a string or use a Play! text template to generate the template
   * @param bodyHtml : pass a string or use a Play! text template to generate the template
   *  like view.Mails.templateText(tags).
   * like view.Mails.templateHtml(tags).
   * @return
   */
  def send(bodyText: String, bodyHtml: String, subject: String, from: String, to: List[String],
           replyTo: List[String]=Nil, cc: List[String]=Nil, bcc: List[String]=Nil, headers: List[(String,String)]=Nil): String = {

    val email = new ImageHtmlEmail()

    email.setHtmlMsg(bodyHtml)
    email.setTextMsg(bodyText)

    //val cid = email.embed(new File(""))

    email.setCharset(charSet)
    email.setSubject(subject)

    setAddress(from)((address, name) => email.setFrom(address, name))
    to.foreach(x => setAddress(x)((address, name) => email.addTo(address, name)))
    replyTo.foreach(x => setAddress(x)((address, name) => email.addReplyTo(address, name)))
    cc.foreach(x => setAddress(x)((address, name) => email.addCc(address, name)))
    bcc.foreach(x => setAddress(x)((address, name) => email.addBcc(address, name)))
    headers.foreach(x => email.addHeader(x._1, x._2))

    email.setHostName(smtpHost)
    email.setSmtpPort(smtpPort)
    email.setSSLOnConnect(smtpSsl)
    email.setStartTLSEnabled(smtpTls)
    for(u <- smtpUser; p <- smtpPass) yield email.setAuthenticator(new DefaultAuthenticator(u, p))
    email.setDebug(false)

    val baseUrl = Play.current.configuration.getString("application.baseUrl").getOrElse("http://dev.moveinmedia.co.uk:9000")
    val resolver = new DataSourceUrlResolver(new URL(baseUrl))
    //val resolver = new DataSourceFileResolver(new File("/"))

    email.setDataSourceResolver( resolver )

    val id = email.send
    Logger.debug(s"Email sent to $to, Subject: $subject, Message id: $id")
    id
  }

  /**
   * Extracts an email address from the given string and passes to the enclosed method.
   *
   * @param emailAddress
   * @param setter
   */
  private def setAddress(emailAddress: String)(setter: (String, String) => Unit) = {

    if (emailAddress != null) {
      try {
        val iAddress = new InternetAddress(emailAddress);
        val address = iAddress.getAddress()
        val name = iAddress.getPersonal()
        setter(address, name)
      }
      catch {
        case e: Exception =>
          setter(emailAddress, null)
      }
    }
  }

}