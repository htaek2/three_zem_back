// sse 연결
// 그냥 sse로 사용되는 url 넣으면 됨.
const eventSource = new EventSource("http://localhost:7770/api/sse/energy");

// 데이터 날라왔을 때 할 거
eventSource.onmessage = function(event) {
  document.getElementById("result").innerHTML += event.data + "<br>";
};

// 오류 났을 때 할 거
eventSource.onerror = function(err) {
  console.error("SSE 연결 오류:", err);
};
