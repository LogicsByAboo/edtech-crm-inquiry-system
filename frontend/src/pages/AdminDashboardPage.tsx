import React, { useState, useEffect, useCallback } from 'react';
import { inquiryService } from '../services/inquiryService';
import { Inquiry, InquiryCategory, InquiryStatus, UserType } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { Pagination } from '../components/Pagination';
import { useAuth } from '../hooks/useAuth';
import { RequestException } from '../services/api';
import {
  Search,
  RotateCcw,
  Inbox,
  Eye,
  X,
  FileQuestion,
  Users,
  Loader2,
  Mail,
  Phone,
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const { admin } = useAuth();

  const [inquiries, setInquiries] = useState<Inquiry[]>([]);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [currentPage, setCurrentPage] = useState<number>(0);
  const pageSize = 10;

  // Filters
  const [category, setCategory] = useState<InquiryCategory | ''>('');
  const [userType, setUserType] = useState<UserType | ''>('');
  const [status, setStatus] = useState<InquiryStatus | ''>('');
  const [searchInput, setSearchInput] = useState<string>('');
  const [appliedSearch, setAppliedSearch] = useState<string>('');

  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);
  const [viewingInquiry, setViewingInquiry] = useState<Inquiry | null>(null);

  const fetchInquiries = useCallback(async (page: number) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await inquiryService.getInquiries({
        category: category || undefined,
        userType: userType || undefined,
        status: status || undefined,
        search: appliedSearch || undefined,
        page,
        size: pageSize,
      });

      setInquiries(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
      setCurrentPage(data.currentPage);
    } catch (err: unknown) {
      if (err instanceof RequestException) {
        setError(err.message);
      } else {
        setError('Failed to load inquiries from server');
      }
    } finally {
      setIsLoading(false);
    }
  }, [category, userType, status, appliedSearch]);

  useEffect(() => {
    fetchInquiries(0);
  }, [fetchInquiries]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setAppliedSearch(searchInput.trim());
  };

  const handleResetFilters = () => {
    setCategory('');
    setUserType('');
    setStatus('');
    setSearchInput('');
    setAppliedSearch('');
  };

  const handleStatusChange = async (inquiryId: number, newStatus: InquiryStatus) => {
    setUpdatingId(inquiryId);
    try {
      const updated = await inquiryService.updateInquiryStatus(inquiryId, newStatus);
      setInquiries((prev) =>
        prev.map((item) => (item.id === inquiryId ? updated : item))
      );
      if (viewingInquiry && viewingInquiry.id === inquiryId) {
        setViewingInquiry(updated);
      }
    } catch (err: unknown) {
      if (err instanceof RequestException) {
        alert(`Status update failed: ${err.message}`);
      } else {
        alert('An unexpected error occurred while updating status.');
      }
    } finally {
      setUpdatingId(null);
    }
  };

  const formatDate = (isoString: string): string => {
    try {
      const date = new Date(isoString);
      return date.toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return isoString;
    }
  };

  return (
    <div>
      {/* Header & Metrics */}
      <div style={{ marginBottom: '1.75rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--slate-900)' }}>
              Inquiry Management Dashboard
            </h1>
            <p style={{ color: 'var(--slate-500)', fontSize: '0.9rem' }}>
              Logged in as <strong>{admin?.name}</strong> ({admin?.email})
            </p>
          </div>
          <button
            onClick={() => fetchInquiries(currentPage)}
            className="btn btn-secondary"
            disabled={isLoading}
            title="Refresh list"
          >
            <RotateCcw size={16} />
            Refresh
          </button>
        </div>

        {/* Stats Summary Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--slate-500)', textTransform: 'uppercase' }}>
                  Total Inquiries
                </div>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--slate-900)', marginTop: '0.25rem' }}>
                  {totalElements}
                </div>
              </div>
              <div style={{ width: '40px', height: '40px', background: 'var(--primary-light)', color: 'var(--primary)', borderRadius: 'var(--radius)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Inbox size={20} />
              </div>
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--slate-500)', marginTop: '0.5rem' }}>
              Database-wide total matching current filters
            </div>
          </div>

          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--slate-500)', textTransform: 'uppercase' }}>
                  Active Page
                </div>
                <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--slate-900)', marginTop: '0.25rem' }}>
                  {inquiries.length} records
                </div>
              </div>
              <div style={{ width: '40px', height: '40px', background: '#f0fdf4', color: '#16a34a', borderRadius: 'var(--radius)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Users size={20} />
              </div>
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--slate-500)', marginTop: '0.5rem' }}>
              Page {totalPages === 0 ? 0 : currentPage + 1} of {totalPages}
            </div>
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1.25rem' }}>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
            <div style={{ flex: '1 1 300px', position: 'relative' }}>
              <input
                type="text"
                className="form-input"
                placeholder="Search by full name or phone number..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
              />
            </div>
            <button type="submit" className="btn btn-primary">
              <Search size={16} />
              Search
            </button>
            {(appliedSearch || category || userType || status) && (
              <button
                type="button"
                onClick={handleResetFilters}
                className="btn btn-secondary"
                title="Clear all filters"
              >
                <X size={16} />
                Reset Filters
              </button>
            )}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '0.75rem', paddingTop: '0.5rem', borderTop: '1px solid var(--slate-200)' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--slate-500)', marginBottom: '0.25rem' }}>
                CATEGORY
              </label>
              <select
                className="form-select"
                value={category}
                onChange={(e) => setCategory(e.target.value as InquiryCategory | '')}
                style={{ padding: '0.45rem 0.65rem', fontSize: '0.85rem' }}
              >
                <option value="">All Categories</option>
                <option value="Scholarship">Scholarship</option>
                <option value="Concert">Concert</option>
                <option value="Admission">Admission</option>
                <option value="General Query">General Query</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--slate-500)', marginBottom: '0.25rem' }}>
                USER TYPE
              </label>
              <select
                className="form-select"
                value={userType}
                onChange={(e) => setUserType(e.target.value as UserType | '')}
                style={{ padding: '0.45rem 0.65rem', fontSize: '0.85rem' }}
              >
                <option value="">All User Types</option>
                <option value="Parent">Parent</option>
                <option value="Student">Student</option>
                <option value="School">School</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 600, color: 'var(--slate-500)', marginBottom: '0.25rem' }}>
                STATUS
              </label>
              <select
                className="form-select"
                value={status}
                onChange={(e) => setStatus(e.target.value as InquiryStatus | '')}
                style={{ padding: '0.45rem 0.65rem', fontSize: '0.85rem' }}
              >
                <option value="">All Statuses</option>
                <option value="New">New</option>
                <option value="Contacted">Contacted</option>
                <option value="Resolved">Resolved</option>
              </select>
            </div>
          </div>
        </form>
      </div>

      {error && (
        <div className="alert alert-danger" style={{ marginBottom: '1.25rem' }}>
          <div>{error}</div>
        </div>
      )}

      {/* Data Table */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: '60px' }}>ID</th>
                <th>Name</th>
                <th>Contact</th>
                <th>User Type</th>
                <th>Category</th>
                <th>Submitted</th>
                <th>Status</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '3rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', color: 'var(--slate-500)' }}>
                      <Loader2 className="animate-spin" size={20} />
                      Loading inquiries...
                    </div>
                  </td>
                </tr>
              ) : inquiries.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', padding: '3rem' }}>
                    <div style={{ color: 'var(--slate-400)', marginBottom: '0.5rem' }}>
                      <FileQuestion size={40} style={{ margin: '0 auto 0.5rem' }} />
                    </div>
                    <div style={{ fontWeight: 600, color: 'var(--slate-700)' }}>No inquiries found</div>
                    <div style={{ fontSize: '0.85rem', color: 'var(--slate-500)', marginTop: '0.25rem' }}>
                      Try adjusting or resetting your search and filter criteria.
                    </div>
                  </td>
                </tr>
              ) : (
                inquiries.map((inquiry) => (
                  <tr key={inquiry.id}>
                    <td style={{ fontWeight: 600, color: 'var(--slate-500)' }}>#{inquiry.id}</td>
                    <td style={{ fontWeight: 600, color: 'var(--slate-900)' }}>{inquiry.name}</td>
                    <td>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.2rem' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--slate-700)' }}>
                          <Mail size={12} color="var(--slate-400)" />
                          {inquiry.email}
                        </span>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--slate-500)', fontSize: '0.8rem' }}>
                          <Phone size={12} color="var(--slate-400)" />
                          {inquiry.phone}
                        </span>
                      </div>
                    </td>
                    <td>
                      <span style={{ fontSize: '0.85rem', fontWeight: 500 }}>{inquiry.userType}</span>
                    </td>
                    <td>
                      <span
                        style={{
                          fontSize: '0.85rem',
                          padding: '0.2rem 0.5rem',
                          background: 'var(--slate-100)',
                          borderRadius: '4px',
                          color: 'var(--slate-700)',
                        }}
                      >
                        {inquiry.category}
                      </span>
                    </td>
                    <td style={{ fontSize: '0.8rem', color: 'var(--slate-500)', whiteSpace: 'nowrap' }}>
                      {formatDate(inquiry.createdAt)}
                    </td>
                    <td>
                      <StatusBadge status={inquiry.status} />
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '0.5rem' }}>
                        {/* Status Quick Update Select */}
                        <select
                          className="form-select"
                          style={{
                            padding: '0.25rem 0.5rem',
                            fontSize: '0.775rem',
                            width: 'auto',
                            minWidth: '110px',
                          }}
                          value={inquiry.status}
                          onChange={(e) => handleStatusChange(inquiry.id, e.target.value as InquiryStatus)}
                          disabled={updatingId === inquiry.id}
                        >
                          <option value="New">New</option>
                          <option value="Contacted">Contacted</option>
                          <option value="Resolved">Resolved</option>
                        </select>

                        <button
                          onClick={() => setViewingInquiry(inquiry)}
                          className="btn btn-secondary"
                          style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                          title="View Inquiry Details"
                        >
                          <Eye size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        <div style={{ padding: '0 1.25rem' }}>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={pageSize}
            onPageChange={(page) => fetchInquiries(page)}
            disabled={isLoading}
          />
        </div>
      </div>

      {/* View Detail Modal */}
      {viewingInquiry && (
        <div className="modal-overlay" onClick={() => setViewingInquiry(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', paddingBottom: '0.75rem', borderBottom: '1px solid var(--slate-200)' }}>
              <div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--slate-900)' }}>
                  Inquiry #{viewingInquiry.id}
                </h3>
                <span style={{ fontSize: '0.8rem', color: 'var(--slate-500)' }}>
                  Submitted on {formatDate(viewingInquiry.createdAt)}
                </span>
              </div>
              <button
                onClick={() => setViewingInquiry(null)}
                className="btn btn-secondary"
                style={{ padding: '0.35rem 0.6rem' }}
              >
                <X size={16} />
              </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1.25rem', fontSize: '0.9rem' }}>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>FULL NAME</span>
                <strong>{viewingInquiry.name}</strong>
              </div>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>STATUS</span>
                <StatusBadge status={viewingInquiry.status} />
              </div>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>EMAIL</span>
                <a href={`mailto:${viewingInquiry.email}`} style={{ color: 'var(--primary)' }}>
                  {viewingInquiry.email}
                </a>
              </div>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>PHONE</span>
                <a href={`tel:${viewingInquiry.phone}`} style={{ color: 'var(--primary)' }}>
                  {viewingInquiry.phone}
                </a>
              </div>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>USER TYPE</span>
                <span>{viewingInquiry.userType}</span>
              </div>
              <div>
                <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600 }}>CATEGORY</span>
                <span>{viewingInquiry.category}</span>
              </div>
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
              <span style={{ color: 'var(--slate-500)', display: 'block', fontSize: '0.75rem', fontWeight: 600, marginBottom: '0.35rem' }}>
                MESSAGE CONTENT
              </span>
              <div
                style={{
                  background: 'var(--slate-50)',
                  border: '1px solid var(--slate-200)',
                  borderRadius: 'var(--radius)',
                  padding: '1rem',
                  fontSize: '0.925rem',
                  lineHeight: 1.6,
                  whiteSpace: 'pre-wrap',
                  color: 'var(--slate-800)',
                }}
              >
                {viewingInquiry.message}
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '1rem', borderTop: '1px solid var(--slate-200)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span style={{ fontSize: '0.85rem', color: 'var(--slate-600)' }}>Update Status:</span>
                <button
                  className="btn btn-secondary"
                  style={{ padding: '0.35rem 0.65rem', fontSize: '0.8rem' }}
                  onClick={() => handleStatusChange(viewingInquiry.id, 'New')}
                  disabled={viewingInquiry.status === 'New' || updatingId === viewingInquiry.id}
                >
                  New
                </button>
                <button
                  className="btn btn-secondary"
                  style={{ padding: '0.35rem 0.65rem', fontSize: '0.8rem' }}
                  onClick={() => handleStatusChange(viewingInquiry.id, 'Contacted')}
                  disabled={viewingInquiry.status === 'Contacted' || updatingId === viewingInquiry.id}
                >
                  Contacted
                </button>
                <button
                  className="btn btn-secondary"
                  style={{ padding: '0.35rem 0.65rem', fontSize: '0.8rem' }}
                  onClick={() => handleStatusChange(viewingInquiry.id, 'Resolved')}
                  disabled={viewingInquiry.status === 'Resolved' || updatingId === viewingInquiry.id}
                >
                  Resolved
                </button>
              </div>

              <button onClick={() => setViewingInquiry(null)} className="btn btn-primary" style={{ padding: '0.45rem 1rem' }}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
