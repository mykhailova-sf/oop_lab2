import type { Role } from "../types/userTypes";

type GenerateCodeProps = {
   role: Role;
};

function GenerateCode({ role }: GenerateCodeProps) {
   return (
      <div className="flex justify-between gap-4 items-center">
         <span className="text-base-content/70">
            Generate code for a {role === "DOCTOR" ? "doctor" : "nurse"}
         </span>
         <div className="flex gap-4 items-center">
            <div
               className="card py-2 px-3 border border-base-content/10 
                  text-center bg-base-200/20 shadow flex flex-row gap-3"
            >
               <span>234581</span>
               <button className="btn btn-xs">copy</button>
            </div>
            <button className="btn">Generate</button>
         </div>
      </div>
   );
}

export { GenerateCode };
