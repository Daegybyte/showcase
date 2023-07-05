from django.shortcuts import render
from django.http import HttpResponse

def home(request):
    '''
    render function takes request object and template path as arguments
    template path is chosen automagically by Django
    can be overridden by specifying a template directory in settings.py
    '''
    return render(request, 'budget_app/home.html') 

def about(request):
    return render(request, 'budget_app/about.html')