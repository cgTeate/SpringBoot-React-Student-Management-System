import axios from 'axios';
// import fetch from 'unfetch';
const url = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL

//method to return all students
const checkStatus = response => {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  // convert non-2xx HTTP responses into errors:
  const error = new Error(response.statusText);
  error.response = response;
  throw error;
};

export const getAllStudents = () =>
  axios.get(`${url}/api/v1/students`)
    .then(checkStatus);


// This code defines a React component that uses React hooks to fetch a list of students from an API and update the component's state.