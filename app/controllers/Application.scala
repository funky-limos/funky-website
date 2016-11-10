package controllers

import models._
import org.joda.time.DateTime
import play.api._
import play.api.data.Form
import play.api.mvc._
import services.Mailer

object Application extends Controller {

  val bookingEnquiryFormMapping = Form(BookingEnquiryForm.mapping)

  implicit val cdnUrl = CdnUrl(Play.current.configuration.getString("cdn.url").get)


  def miniBus = Action { implicit request =>
    Ok(views.html.miniBus())
  }

  def index(enquirySubmitted: Option[Boolean]) = Action { implicit request =>
    Ok(views.html.index(enquirySubmitted.getOrElse(false)))
  }

  def busHire = Action { implicit request =>
    Ok(views.html.busHire())
  }

  def coachHire = Action { implicit request =>
    Ok(views.html.coachHire())
  }

  def airportTransfers = Action { implicit request =>
    Ok(views.html.airportTransfers())
  }

  def crew = Action { implicit request =>
    Ok(views.html.crew())
  }

  def gallery = Action { implicit request =>
    Ok(views.html.gallery())
  }

  def contact = Action { implicit request =>
    Ok(views.html.contact(bookingEnquiryFormMapping))
  }

  def processContact = Action { implicit request =>
    bookingEnquiryFormMapping.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.contact(formWithErrors))
      },
      bookingEnquiryForm => {
        Mailer.mailContactForm(bookingEnquiryForm)
        Redirect(controllers.routes.Application.index(Some(true))).flashing("success" -> "Thanks for your enquiry!")
      }
    )
  }



}