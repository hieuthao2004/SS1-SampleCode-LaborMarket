Nguyên lý ghép cặp **triplet stable matching** trong bài toán này mở rộng từ bài toán **ghép cặp ổn định (Stable Marriage Problem)** cơ bản, nhưng thay vì chỉ ghép đôi hai nhóm đối tượng (ví dụ, nam và nữ), chúng ta sẽ ghép ba nhóm đối tượng cùng một lúc.

### Mục tiêu chính:
- Ghép cặp ba nhóm đối tượng (A, B, C) sao cho đạt được sự ổn định và cân bằng về độ hài lòng của tất cả các thành viên tham gia.
- **Ổn định** ở đây có nghĩa là không tồn tại một nhóm đối tượng nào (A, B, hoặc C) có thể cải thiện vị trí của mình bằng cách từ bỏ ghép hiện tại và chọn một ghép cặp khác tốt hơn.
- **Độ hài lòng** (satisfaction) đo lường mức độ ưu tiên của mỗi người trong nhóm đối với ghép cặp của họ. Độ ưu tiên càng cao (vị trí càng gần đầu danh sách) thì mức độ hài lòng càng lớn.

### Quy trình ghép cặp:

1. **Các đối tượng và danh sách ưu tiên**:
   - Mỗi đối tượng trong nhóm A có một danh sách thứ tự ưu tiên về những người họ muốn ghép trong nhóm B.
   - Tương tự, nhóm B có danh sách ưu tiên về những người trong nhóm A, và nhóm C cũng có danh sách ưu tiên về cả A và B.

2. **Bắt đầu với tất cả các thành viên nhóm A chưa được ghép cặp**:
   - Lúc đầu, tất cả các thành viên của nhóm A đều tự do (chưa ghép).
   - Trong quá trình ghép cặp, thành viên của nhóm A sẽ chọn một người trong nhóm B theo thứ tự ưu tiên của họ.

3. **Xét ghép cặp giữa nhóm A và B**:
   - Nếu một thành viên trong nhóm B đang chưa được ghép cặp, họ sẽ tạm thời ghép với người từ nhóm A.
   - Nếu họ đã có một ghép cặp với một thành viên A khác, thì họ sẽ so sánh người mới với người hiện tại:
     - Nếu họ thích người mới hơn, họ sẽ từ bỏ ghép cặp hiện tại và chọn người mới.
     - Người bị từ bỏ sẽ quay trở lại danh sách tự do của nhóm A.

4. **Ghép cặp với nhóm C**:
   - Để tạo thành một bộ ba, chúng ta giả định rằng người trong nhóm C được ghép cặp dựa trên mối liên hệ của họ với A và B. Nhóm C cũng có quyền ưu tiên trong việc chọn cặp A và B giống như cách nhóm B chọn A.
   
5. **Tính toán độ hài lòng và cân bằng**:
   - Mỗi thành viên trong các nhóm A, B và C có một độ hài lòng riêng, dựa trên thứ tự họ chọn người ghép trong danh sách ưu tiên của mình.
   - **Cân bằng** độ hài lòng được tính dựa trên sự chênh lệch giữa độ hài lòng của các thành viên trong nhóm. Sự chênh lệch càng nhỏ thì cặp ghép càng cân bằng.

6. **Mục tiêu tối ưu hóa**:
   - Trong quá trình này, thuật toán không chỉ tìm cách tối đa số lượng ghép cặp ổn định (triplet) mà còn cố gắng tối thiểu sự chênh lệch độ hài lòng giữa các đối tượng, tức là làm cho ghép cặp được **cân bằng** nhất có thể.

### Ví dụ cụ thể:
- Một người A có thể thích ghép với người B1, nhưng nếu B1 đang ghép với người A2 và thích A2 hơn, thì A sẽ phải thử chọn một người khác trong nhóm B.
- Quá trình tiếp diễn cho đến khi không ai muốn đổi ghép cặp nữa, và kết quả là một bộ ba (A, B, C) được ghép sao cho không ai có thể cải thiện tình hình của mình bằng cách đổi ghép.

Nguyên lý này được áp dụng vào thuật toán tối ưu đa mục tiêu NSGA-II để tìm ra các ghép cặp ổn định và cân bằng nhất, dựa trên hàm mục tiêu và đánh giá sự hài lòng.