package scala.akka.example

import akka.actor.ActorRef
import akka.event.LoggingReceive
import akka.actor.Actor

object TravelAgent {
  case class Booktrip(transport: ActorRef, accomodation: ActorRef, nbOfPersons: Int)
  case object Done
  case object Failed
}

class TravelAgent extends Actor {
  import TravelAgent._

  def receive = LoggingReceive {
    case Booktrip(flightAgent, hotelAgent, persons) =>
      flightAgent ! Flight.BookSeat(persons)
      hotelAgent ! Hotel.BookRoom(persons)
      context.become(awaitTransportOrAccomodation(flightAgent, hotelAgent, sender))
  }

  def awaitTransportOrAccomodation(transport: ActorRef, accomodation: ActorRef, customer: ActorRef): Receive = LoggingReceive {
    case Flight.Done =>
      context.become(awaitAccomodation(customer))
    case Hotel.Done =>
      context.become(awaitTransport(customer))
    case Flight.Failed | Hotel.Failed =>
      customer ! Failed
      context.stop(self)
  }

  def awaitTransport(customer: ActorRef): Receive = LoggingReceive {
    case Flight.Done =>
      customer ! Done
      context.stop(self)
    case Flight.Failed =>
      customer ! Failed
      context.stop(self)
  }

  def awaitAccomodation(customer: ActorRef): Receive = LoggingReceive {
    case Hotel.Done =>
      customer ! Done
      context.stop(self)
    case Hotel.Failed =>
      customer ! Failed
      context.stop(self)
  }
}