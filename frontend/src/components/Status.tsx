import type { AppointmentResponse } from "../types/appointmentTypes";

function StatusBar({
   status
}: {
   status: AppointmentResponse["status"];
}) {
   if (status === "pending") {
      return (
         <p className="card text-primary-content py-0.5 px-4 border border-warning/30 text-center bg-warning/30">
            Pending
         </p>
      );
   }

   if (status === "declined") {
      return (
         <p className="card text-error-content py-0.5 px-4 border border-error/30 text-center bg-error/30">
            Declined
         </p>
      );
   }

   if (status === "completed") {
      return (
         <p className="card text-success-content py-0.5 px-4 border border-success/30 text-center bg-success/30">
            Completed
         </p>
      );
   }

   return null;
}

export {StatusBar}