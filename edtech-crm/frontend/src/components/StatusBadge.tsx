import React from 'react';
import { InquiryStatus } from '../types';
import { Clock, PhoneCall, CheckCircle } from 'lucide-react';

interface StatusBadgeProps {
  status: InquiryStatus;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  switch (status) {
    case 'New':
      return (
        <span className="badge badge-new">
          <Clock size={12} />
          New
        </span>
      );
    case 'Contacted':
      return (
        <span className="badge badge-contacted">
          <PhoneCall size={12} />
          Contacted
        </span>
      );
    case 'Resolved':
      return (
        <span className="badge badge-resolved">
          <CheckCircle size={12} />
          Resolved
        </span>
      );
    default:
      return <span className="badge">{status}</span>;
  }
};
