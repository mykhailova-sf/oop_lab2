import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, useNavigate } from "react-router";
import { routes } from "../../config/routes.config";
import { postLogin } from "../../api/mutations";
import { useQueryClient } from "@tanstack/react-query";
import { queryKeys } from "../../config/queryKeys";

const loginSchema = z.object({
   email: z.string().email({ message: "Invalid email address" }),
   password: z
      .string()
      .min(6, { message: "Password must be at least 6 characters" })
});

function wait(seconds: number): Promise<void> {
   return new Promise((resolve) => setTimeout(resolve, seconds * 1000));
}
type LoginFormInputs = z.infer<typeof loginSchema>;

function LoginPage() {
   const {
      register,
      handleSubmit,
      formState: { errors, isSubmitting }
   } = useForm<LoginFormInputs>({
      resolver: zodResolver(loginSchema)
   });

   const navigate = useNavigate();

   const queryClient = useQueryClient();

   const onSubmit = async (data: LoginFormInputs) => {
      await postLogin(data);
      queryClient.invalidateQueries({ queryKey: queryKeys.currentUser });
      await wait(1);
      navigate(routes.profile);
   };

   return (
      <div className="flex items-center justify-center h-full ">
         <div className="w-full max-w-sm p-8 shadow-lg card border border-base-300">
            <h2 className="mb-4 text-2xl font-bold text-center">Login</h2>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
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
                  {isSubmitting ? "Logging in..." : "Login"}
               </button>
               <div className="text-center">
                  <span>Don't have an account?</span>{" "}
                  <Link to={routes.reg} className="text-secondary link">
                     Registration
                  </Link>
               </div>
            </form>
         </div>
      </div>
   );
}
export { LoginPage };
