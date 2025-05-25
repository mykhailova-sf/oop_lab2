import { useRef, type PropsWithChildren } from "react";

type ModalProps = {
   trigger: PropsWithChildren["children"];
   content: PropsWithChildren["children"];
};

function Modal({ trigger, content }: ModalProps) {
   const modalRef = useRef<HTMLDialogElement | null>(null);

   return (
      <div>
         {/* You can open the modal using document.getElementById('ID').showModal() method */}
         <div onClick={() => modalRef.current && modalRef.current.showModal()}>
            {trigger}
         </div>
         <dialog ref={modalRef} className="modal">
            <div className="modal-box">{content}</div>
            <CloseOutside />
         </dialog>
      </div>
   );
}

function CloseOutside() {
   return (
      <form method="dialog" className="modal-backdrop">
         <button />
      </form>
   );
}

const useCloseModal = () => {
   const closeModalButtonRef = useRef<HTMLButtonElement | null>(null);
   const closeModal = () => {
      if (closeModalButtonRef.current) closeModalButtonRef.current.click();
   };

   return { closeModalButtonRef, closeModal };
};

export { Modal, useCloseModal };
