package models

import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Forms
import scala.util.control.Exception._

/**
 * Created by nic on 21/10/2014.
 */
case class BookingEnquiryForm(
  fullName: String,
  email: String,
  telephone: String,
  date: (Int, Int, Int),
  vehicle: String,
  address: String,
  comments: Option[String]
) {
  import BookingEnquiryForm._
  def getDate = tripleToJodaDateTime(date)
}

object BookingEnquiryForm {
  val mapping = Forms.mapping(
    "fullName" -> nonEmptyText,
    "email" -> email,
    "telephone" -> nonEmptyText,
    "date" -> Forms.tuple(
      "day" -> number,
      "month" -> number,
      "year" -> number
    ).verifying("Failed form constraints!", t => allCatch.opt(tripleToJodaDateTime(t)).isDefined),
    "vehicle" -> nonEmptyText,
    "address" -> nonEmptyText,
    "comments" -> optional(text)
  )(BookingEnquiryForm.apply)(BookingEnquiryForm.unapply)

  def tripleToJodaDateTime(t: (Int, Int, Int)): DateTime = new DateTime(t._3, t._2, t._1, 0, 0, 0)
}