const LABELS = {
  CHO_XAC_NHAN: 'Chờ xác nhận',
  DANG_MUON: 'Đang mượn',
  QUA_HAN: 'Quá hạn',
  DA_TRA: 'Đã trả',
  HUY: 'Đã hủy',
};

const COLOR = {
  CHO_XAC_NHAN: 'warning',
  DANG_MUON: 'primary',
  QUA_HAN: 'danger',
  DA_TRA: 'secondary',
  HUY: 'dark',
};

export default function StatusBadge({ status }) {
  const color = COLOR[status] || 'light';
  return (
    <span className={`badge bg-${color}`}>
      {LABELS[status] || status}
    </span>
  );
}
