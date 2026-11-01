export type UserType = 'Parent' | 'Student' | 'School';

export type InquiryCategory = 'Scholarship' | 'Concert' | 'Admission' | 'General Query';

export type InquiryStatus = 'New' | 'Contacted' | 'Resolved';

export interface Inquiry {
  id: number;
  name: string;
  email: string;
  phone: string;
  userType: UserType;
  category: InquiryCategory;
  message: string;
  status: InquiryStatus;
  createdAt: string;
}

export interface InquirySubmission {
  name: string;
  email: string;
  phone: string;
  userType: UserType;
  category: InquiryCategory;
  message: string;
}

export interface Admin {
  id: number;
  name: string;
  email: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  admin: Admin;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface ApiError {
  timestamp?: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  details?: Record<string, string>;
}
