import { apiClient } from './api';
import { Inquiry, InquiryCategory, InquiryStatus, InquirySubmission, PageResponse, UserType } from '../types';

export interface InquiryFilterParams {
  category?: InquiryCategory | '';
  userType?: UserType | '';
  status?: InquiryStatus | '';
  search?: string;
  page?: number;
  size?: number;
}

export const inquiryService = {
  async submitInquiry(submission: InquirySubmission): Promise<Inquiry> {
    return apiClient<Inquiry>('/inquiries', {
      method: 'POST',
      body: JSON.stringify(submission),
    });
  },

  async getInquiries(params: InquiryFilterParams = {}): Promise<PageResponse<Inquiry>> {
    const query = new URLSearchParams();

    if (params.category) query.append('category', params.category);
    if (params.userType) query.append('userType', params.userType);
    if (params.status) query.append('status', params.status);
    if (params.search && params.search.trim()) query.append('search', params.search.trim());
    if (typeof params.page === 'number') query.append('page', params.page.toString());
    if (typeof params.size === 'number') query.append('size', params.size.toString());

    const qs = query.toString();
    const endpoint = `/inquiries${qs ? `?${qs}` : ''}`;
    return apiClient<PageResponse<Inquiry>>(endpoint, {
      method: 'GET',
    });
  },

  async getInquiryById(id: number): Promise<Inquiry> {
    return apiClient<Inquiry>(`/inquiries/${id}`, {
      method: 'GET',
    });
  },

  async updateInquiryStatus(id: number, status: InquiryStatus): Promise<Inquiry> {
    return apiClient<Inquiry>(`/inquiries/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    });
  },
};
