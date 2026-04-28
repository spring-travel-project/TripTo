<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<dialog id="customModal" class="modal">
    <div class="modal-box text-center">
        <h3 class="font-bold text-lg mb-2">알림</h3>
        <p class="py-4 text-slate-600" id="modalMessage">여기에 알림 메시지가 들어갑니다.</p>
        <div class="modal-action justify-center mt-2">
            <form method="dialog">
                <button class="btn btn-neutral w-24">확인</button>
            </form>
        </div>
    </div>
</dialog>

<script>
    // 공통 알림 모달 띄우기 함수
    function showAlert(msg) {
        $('#modalMessage').text(msg); 
        document.getElementById('customModal').showModal(); 
    }
</script>