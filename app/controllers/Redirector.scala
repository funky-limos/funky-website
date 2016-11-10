package controllers

import models._

import play.api._
import play.api.data.Form
import play.api.mvc._


object Redirector extends Controller {

  def redirect(file: String) = Action { implicit request =>
    file match {
      case "partypackages.php"             => Redirect(controllers.routes.Application.busHire.url, MOVED_PERMANENTLY)
      case "funky-bus-party-packages.php"  => Redirect(controllers.routes.Application.busHire.url, MOVED_PERMANENTLY)
      case "ateam-van-limo-hire.php"       => Redirect(controllers.routes.Application.busHire.url, MOVED_PERMANENTLY)
      case "contactus.php"                 => Redirect(controllers.routes.Application.contact.url, MOVED_PERMANENTLY)
      case "yourcrew.php"                  => Redirect(controllers.routes.Application.crew.url, MOVED_PERMANENTLY)
      case "gallery.php"                   => Redirect(controllers.routes.Application.gallery.url, MOVED_PERMANENTLY)
      case "gallery-kids.php"              => Redirect(controllers.routes.Application.gallery.url, MOVED_PERMANENTLY)
      case "gallery-18s.php"               => Redirect(controllers.routes.Application.gallery.url, MOVED_PERMANENTLY)
      case "wedding-limo-hire.php"         => Redirect(controllers.routes.Application.coachHire.url, MOVED_PERMANENTLY)
      case "Airport-Transfer-Minibus.php"  => Redirect(controllers.routes.Application.coachHire.url, MOVED_PERMANENTLY)
      case _                               => Redirect(controllers.routes.Application.index(None).url, MOVED_PERMANENTLY)
    }
  }

}


