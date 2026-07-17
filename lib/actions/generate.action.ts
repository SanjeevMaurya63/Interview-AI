"use server";

const BACKEND_URL = "http://localhost:8080";

export async function getInterviewByUserId(userId: string): Promise<Interview[] | null> {
    try {
        const res = await fetch(`${BACKEND_URL}/api/interviews/user/${userId}`, {
            cache: 'no-store'
        });
        if (!res.ok) return null;
        return await res.json();
    } catch (e) {
        console.error("Error fetching interviews by userId:", e);
        return null;
    }
} 

export async function getLatestInterviews(params: GetLatestInterviewsParams): Promise<Interview[] | null> {
    try {
        const res = await fetch(`${BACKEND_URL}/api/interviews/latest?userId=${params.userId}`, {
            cache: 'no-store'
        });
        if (!res.ok) return null;
        return await res.json();
    } catch (e) {
        console.error("Error fetching latest interviews:", e);
        return null;
    }
} 

export async function getInterviewById(id: string): Promise<Interview | null> {
    try {
        const res = await fetch(`${BACKEND_URL}/api/interviews/${id}`, {
            cache: 'no-store'
        });
        if (!res.ok) return null;
        return await res.json();
    } catch (e) {
        console.error("Error fetching interview by ID:", e);
        return null;
    }
} 

export async function createFeedback(params: CreateFeedbackParams) {
    try {
        const res = await fetch(`${BACKEND_URL}/api/feedback/create`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(params)
        });
        if (!res.ok) return { success: false };
        return await res.json();
    } catch (e) {
        console.error("Error creating feedback:", e);
        return { success: false };
    }
}

export async function getFeedbackByInterviewId(
    params: GetFeedbackByInterviewIdParams
): Promise<Feedback | null> {
    try {
        const res = await fetch(`${BACKEND_URL}/api/feedback/interview/${params.interviewId}?userId=${params.userId}`, {
            cache: 'no-store'
        });
        if (!res.ok) return null;
        return await res.json();
    } catch (e) {
        console.error("Error fetching feedback by interview id:", e);
        return null;
    }
}