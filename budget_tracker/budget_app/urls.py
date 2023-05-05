from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='budget_app-home'),
    path('about/', views.about, name='budget_app-about'),
]