import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router";
import { routes } from "../../config/routes.config";
import { postRegistration } from "../../api/mutations";

const registrationSchema = z.object({
   firstName: z.string().min(2, { message: "First name is too short" }),
   lastName: z.string().min(2, { message: "Last name is too short" }),
   email: z.string().email({ message: "Invalid email address" }),
   password: z
      .string()
      .min(6, { message: "Password must be at least 6 characters" })
});

type RegistrationFormInputs = z.infer<typeof registrationSchema>;

function RegistrationPage() {
   const {
      register,
      handleSubmit,
      formState: { errors, isSubmitting }
   } = useForm<RegistrationFormInputs>({
      resolver: zodResolver(registrationSchema)
   });

   const navigate = useNavigate();

   const onSubmit = async (data: RegistrationFormInputs) => {
      await postRegistration({ role: "PATIENT", ...data });
      navigate(routes.profile);
   };

   return (
      <div className="flex items-center justify-center h-full">
         <div className="w-full max-w-sm p-8 shadow-lg card border border-base-300">
            <h2 className="mb-4 text-2xl font-bold text-center">
               Registration
            </h2>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
               <div className="form-control">
                  <label className="label">
                     <span className="label-text">First Name</span>
                  </label>
                  <input
                     type="text"
                     placeholder="John"
                     className={`input input-bordered ${
                        errors.firstName ? "input-error" : ""
                     }`}
                     {...register("firstName")}
                  />
                  {errors.firstName && (
                     <span className="mt-1 text-sm text-error">
                        {errors.firstName.message}
                     </span>
                  )}
               </div>

               <div className="form-control">
                  <label className="label">
                     <span className="label-text">Last Name</span>
                  </label>
                  <input
                     type="text"
                     placeholder="Doe"
                     className={`input input-bordered ${
                        errors.lastName ? "input-error" : ""
                     }`}
                     {...register("lastName")}
                  />
                  {errors.lastName && (
                     <span className="mt-1 text-sm text-error">
                        {errors.lastName.message}
                     </span>
                  )}
               </div>

               <div className="form-control">
                  <label className="label">
                     <span className="label-text">Email</span>
                  </label>
                  <input
                     type="email"
                     placeholder="email@example.com"
                     className={`input input-bordered ${
                        errors.email ? "input-error" : ""
                     }`}
                     {...register("email")}
                  />
                  {errors.email && (
                     <span className="mt-1 text-sm text-error">
                        {errors.email.message}
                     </span>
                  )}
               </div>

               <div className="form-control">
                  <label className="label">
                     <span className="label-text">Password</span>
                  </label>
                  <input
                     type="password"
                     placeholder="••••••••"
                     className={`input input-bordered ${
                        errors.password ? "input-error" : ""
                     }`}
                     {...register("password")}
                  />
                  {errors.password && (
                     <span className="mt-1 text-sm text-error">
                        {errors.password.message}
                     </span>
                  )}
               </div>

               <button
                  type="submit"
                  className="btn btn-secondary w-full"
                  disabled={isSubmitting}
               >
                  {isSubmitting ? "Registering..." : "Submit"}
               </button>
               <div className="text-center">
                  <span>Already have an account?</span>{" "}
                  <Link to={routes.login} className="text-secondary link">
                     Login
                  </Link>
               </div>
            </form>
         </div>
      </div>
   );
}

export { RegistrationPage };
