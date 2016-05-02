package scala.akka.example

import akka.actor.Props
import akka.actor.Actor
import akka.event.LoggingReceive

class BookingMain extends Actor {
  val flight = context.actorOf(Props[Flight], "Aerolineas-Argentinas")
  val hotel = context.actorOf(Props[Hotel], "Sheraton")
  val travelAgent = context.actorOf(Props[TravelAgent], "Despegar")
  travelAgent ! TravelAgent.Booktrip(flight, hotel, 10)
  
  def receive = LoggingReceive {
    case TravelAgent.Done => 
      println("Booking Successful")
      context.stop(self)
    case TravelAgent.Failed =>
      println("Booking Failed")
      context.stop(self)
  }
}